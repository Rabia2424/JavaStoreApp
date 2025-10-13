document.addEventListener('DOMContentLoaded', function () {
    const shippingPreview = document.getElementById('shippingPreview');
    const billingPreview  = document.getElementById('billingPreview');
    const sameAsShipping  = document.getElementById('sameAsShipping');

    const shipField = document.getElementById('shippingAddressField');
    const shipPhone = document.getElementById('shippingPhoneField');
    const billField = document.getElementById('billingAddressField');


    function toggleActive(radio) {
        const name = radio.getAttribute('name');
        document.querySelectorAll('input[name="'+name+'"]').forEach(r => {
            const card = r.closest('.addr-card');
            if (card) card.classList.remove('active');
        });
        const card = radio.closest('.addr-card');
        if (card) card.classList.add('active');
    }

    function toText(radio) {
        const parts = [
            radio.getAttribute('data-full') || '',
            radio.getAttribute('data-line1') || '',
            radio.getAttribute('data-line2') || '',
            radio.getAttribute('data-district') || '',
            radio.getAttribute('data-city') || '',
            radio.getAttribute('data-country') || '',
            '(' + (radio.getAttribute('data-postal') || '') + ')'
        ].filter(Boolean);
        return parts.join(', ');
    }


    document.querySelectorAll('input[name="shippingAddressId"]').forEach(r => {
        r.addEventListener('change', (e) => {
            toggleActive(e.target);
            const txt = toText(e.target);
            shippingPreview.textContent = txt;
            shipField.value = txt;
            shipPhone.value = e.target.getAttribute('data-phone') || '';

            if (sameAsShipping.checked) {
                billingPreview.textContent = txt;
                billField.value = txt;
            }
        });
        const card = r.closest('.addr-card');
        card && card.addEventListener('click', () => r.click());
    });


    document.querySelectorAll('input[name="billingAddressId"]').forEach(r => {
        r.addEventListener('change', (e) => {
            toggleActive(e.target);
            const txt = toText(e.target);
            billingPreview.textContent = txt;
            billField.value = txt;
        });
        const card = r.closest('.addr-card');
        card && card.addEventListener('click', () => r.click());
    });

    function applySameAsShippingState() {
        const disabled = sameAsShipping.checked;
        document.querySelectorAll('input[name="billingAddressId"]').forEach(r => {
            r.disabled = disabled;
            r.required = !disabled;
            if (disabled) r.checked = false;
        });

        if (disabled) {
            const sel = document.querySelector('input[name="shippingAddressId"]:checked');
            if (sel) {
                const txt = toText(sel);
                billingPreview.textContent = txt;
                billField.value = txt;
                document.querySelectorAll('input[name="billingAddressId"]').forEach(r => {
                    r.disabled = disabled;
                    const card = r.closest('.addr-card');
                    if (card) card.classList.remove('active');
                });
            } else {
                billingPreview.textContent = 'Same as shipping';
                billField.value = '';
            }
        } else {
            billingPreview.textContent = 'Not selected';
            billField.value = '';
        }
    }

    sameAsShipping.addEventListener('change', applySameAsShippingState);
    // init on load
    applySameAsShippingState();
})