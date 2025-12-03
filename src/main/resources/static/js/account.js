document.addEventListener('DOMContentLoaded', function () {

    console.log('account.js DOM ready');

    const links = document.querySelectorAll('.account-nav .account-link');
    const content = document.getElementById('accountContent');

    console.log('account.js: links =', links.length, 'content =', !!content);

    if (!links.length || !content) {
        console.warn('account.js: links or content not found');
        return;
    }

    // Initialize links when overview loads
    const initialSection = document.querySelector('.account-link.active')?.getAttribute('data-section');
    if (initialSection === 'overview') {
        initOverviewLinks();
    }

    links.forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();

            const section = this.getAttribute('data-section');
            if (!section) return;

            links.forEach(l => l.classList.remove('active'));
            this.classList.add('active');

            content.innerHTML =
                '<div class="empty-state">Loading...</div>';

            fetch('/account/section/' + section, {
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(res => {
                    if (!res.ok) throw new Error('Request failed: ' + res.status);
                    return res.text();
                })
                .then(html => {
                    content.innerHTML = html;
                    //Address Modal function called
                    if (section === 'addresses') {
                        console.log('Addresses section loaded, calling initAddressModal...');
                        initAddressModal();
                    } else if(section === 'overview'){
                        console.log('Overview section loaded, initializing links...');
                        initOverviewLinks();
                    } else if (section === 'favorites') {
                        console.log('Favorites section loaded, initializing buttons...');
                        initFavorites();
                    }
                })
                .catch(err => {
                    console.error(err);
                    content.innerHTML =
                        '<div class="empty-state">Something went wrong. Please try again.</div>';
                });
        });
    });

    // This is for Address Modal and is called in the upper section
    function initAddressModal() {
        console.log('initAddressModal called');

        setTimeout(() => {
            const modal = document.getElementById('addressModal');
            const openBtn = document.getElementById('openAddressModal');
            const closeBtn = document.getElementById('closeModal');
            const cancelBtn = document.getElementById('cancelBtn');
            const overlay = document.querySelector('.modal-overlay');
            const form = document.getElementById('addressForm');
            const modalTitle = document.getElementById('modalTitle');

            console.log('Modal elements found:', {
                modal: !!modal,
                openBtn: !!openBtn,
                closeBtn: !!closeBtn,
                cancelBtn: !!cancelBtn,
                overlay: !!overlay,
                form: !!form
            });

            if (!modal || !openBtn || !closeBtn || !cancelBtn || !overlay || !form) {
                console.error('Some modal elements not found!');
                return;
            }

            openBtn.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('Opening modal for new address...');
                modalTitle.textContent = 'Add New Address';
                document.getElementById('addressId').value = '';
                form.reset();
                document.getElementById('country').value = 'Turkey';
                modal.classList.add('active');
                document.body.style.overflow = 'hidden';
            });

            document.querySelectorAll('.edit-address-btn').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    console.log('Opening modal for edit...');

                    const data = e.currentTarget.dataset;
                    modalTitle.textContent = 'Edit Address';

                    document.getElementById('addressId').value = data.id;
                    document.getElementById('fullName').value = data.fullname || '';
                    document.getElementById('line1').value = data.line1 || '';
                    document.getElementById('line2').value = data.line2 || '';
                    document.getElementById('district').value = data.district || '';
                    document.getElementById('city').value = data.city || '';
                    document.getElementById('country').value = data.country || 'Turkey';
                    document.getElementById('postalCode').value = data.postalcode || '';
                    document.getElementById('phone').value = data.phone || '';
                    document.getElementById('defaultShipping').checked = data.defaultshipping === 'true';
                    document.getElementById('defaultBilling').checked = data.defaultbilling === 'true';

                    modal.classList.add('active');
                    document.body.style.overflow = 'hidden';
                });
            });

            document.querySelectorAll('.delete-address-btn').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    e.preventDefault();
                    const addressId = e.currentTarget.dataset.id;

                    if (confirm('Are you sure you want to delete this address?')) {
                        console.log('Deleting address:', addressId);

                        try {
                            const response = await fetch(`/address/delete/${addressId}`, {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            });

                            if (response.ok) {
                                const result = await response.json();
                                alert(result.message || 'Address deleted successfully!');

                                links.forEach(l => {
                                    if (l.getAttribute('data-section') === 'addresses') {
                                        l.click();
                                    }
                                });
                            } else {
                                const errorData = await response.json().catch(() => ({ message: 'Unknown error' }));
                                alert('Error: ' + (errorData.message || 'Could not delete address'));
                            }
                        } catch (error) {
                            console.error('Network error:', error);
                            alert('Network error: ' + error.message);
                        }
                    }
                });
            });

            function closeModal() {
                console.log('Closing modal...');
                modal.classList.remove('active');
                document.body.style.overflow = '';
                form.reset();
                document.getElementById('addressId').value = '';
            }

            closeBtn.addEventListener('click', (e) => {
                e.preventDefault();
                closeModal();
            });

            cancelBtn.addEventListener('click', (e) => {
                e.preventDefault();
                closeModal();
            });

            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) {
                    closeModal();
                }
            });

            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                console.log('Form submitting...');

                const addressId = document.getElementById('addressId').value;
                const isEdit = addressId !== '';

                const formData = {
                    fullName: document.getElementById('fullName').value,
                    line1: document.getElementById('line1').value,
                    line2: document.getElementById('line2').value || '',
                    district: document.getElementById('district').value,
                    city: document.getElementById('city').value,
                    country: document.getElementById('country').value,
                    postalCode: document.getElementById('postalCode').value,
                    phone: document.getElementById('phone').value,
                    defaultShipping: document.getElementById('defaultShipping').checked,
                    defaultBilling: document.getElementById('defaultBilling').checked
                };

                console.log('Sending data:', formData);

                try {
                    const url = isEdit ? `/address/update/${addressId}` : '/address/save';
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(formData)
                    });

                    console.log('Response status:', response.status);

                    if (response.ok) {
                        const result = await response.json();
                        alert(result.message || (isEdit ? 'Address updated successfully!' : 'Address saved successfully!'));
                        closeModal();

                        links.forEach(l => {
                            if (l.getAttribute('data-section') === 'addresses') {
                                l.click();
                            }
                        });
                    } else {
                        const errorData = await response.json().catch(() => ({ message: 'Unknown error' }));
                        console.error('Error response:', errorData);
                        alert('Error: ' + (errorData.message || 'Could not save address'));
                    }
                } catch (error) {
                    console.error('Network error:', error);
                    alert('Network error: ' + error.message);
                }
            });

            console.log('Address modal initialized successfully!');
        }, 150);
    }


    function initOverviewLinks() {
        const viewFavoritesLink = document.querySelector('.view-favorites-link');
        if (viewFavoritesLink) {
            viewFavoritesLink.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('View all favorites clicked');

                links.forEach(link => {
                    if (link.getAttribute('data-section') === 'favorites') {
                        link.click();
                    }
                });
            });
        }
    }

    function initFavorites() {
        console.log('initFavorites called');

        setTimeout(() => {
            document.querySelectorAll('.remove-favorite-btn').forEach(btn => {
                btn.addEventListener('click', async function(e) {
                    e.preventDefault();

                    const productId = this.dataset.id;
                    const productName = this.dataset.name;

                    if (!confirm(`Remove "${productName}" from favorites?`)) {
                        return;
                    }

                    console.log('Removing favorite:', productId);

                    this.disabled = true;
                    this.textContent = 'Removing...';

                    try {
                        const response = await fetch(`/favorites/remove/${productId}`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        });

                        if (response.ok) {
                            const result = await response.json();

                            links.forEach(l => {
                                if (l.getAttribute('data-section') === 'favorites') {
                                    l.click();
                                }
                            });
                        } else {
                            const errorData = await response.json().catch(() => ({
                                message: 'Unknown error'
                            }));
                            alert('Error: ' + (errorData.message || 'Could not remove from favorites'));

                            this.disabled = false;
                            this.textContent = 'Remove';
                        }
                    } catch (error) {
                        console.error('Network error:', error);
                        alert('Network error: ' + error.message);

                        this.disabled = false;
                        this.textContent = 'Remove';
                    }
                });
            });

            document.querySelectorAll('.add-to-cart-btn').forEach(btn => {
                btn.addEventListener('click', async function(e) {
                    e.preventDefault();

                    const productId = this.dataset.id;
                    const productName = this.dataset.name;

                    console.log('Adding to cart:', productId);

                    const originalText = this.textContent;
                    this.disabled = true;
                    this.textContent = 'Adding...';

                    try {
                        const response = await fetch(`/cart/add-json/${productId}`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        });

                        if (response.ok) {
                            const result = await response.json();

                            this.textContent = '✓ Added!';
                            this.classList.add('add-to-cart-btn:hover');

                            setTimeout(() => {
                                this.disabled = false;
                                this.textContent = originalText;
                                this.classList.remove('add-to-cart-btn:hover');
                            }, 2000);

                            updateCartCount();

                        } else {
                            const errorData = await response.json().catch(() => ({
                                message: 'Unknown error'
                            }));
                            alert('Error: ' + (errorData.message || 'Could not add to cart'));

                            this.disabled = false;
                            this.textContent = originalText;
                        }
                    } catch (error) {
                        console.error('Network error:', error);
                        alert('Network error: ' + error.message);

                        this.disabled = false;
                        this.textContent = originalText;
                    }
                });
            });

            console.log('Favorites buttons initialized:', {
                removeButtons: document.querySelectorAll('.remove-favorite-btn').length,
                addToCartButtons: document.querySelectorAll('.add-to-cart-btn').length
            });
        }, 100);
    }

    function updateCartCount() {
        const cartCountEl = document.querySelector('.cart-badge');
        if (cartCountEl) {
            fetch('/cart/count')
                .then(res => res.json())
                .then(data => {
                    if (data.count !== undefined) {
                        cartCountEl.textContent = data.count;
                        cartCountEl.style.display = data.count > 0 ? 'inline' : 'none';
                    }
                })
                .catch(err => console.error('Could not update cart count:', err));
        }
    }
});
