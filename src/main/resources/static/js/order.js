document.addEventListener('DOMContentLoaded', function () {
    const shippingPreview = document.getElementById('shippingPreview');
    const billingPreview  = document.getElementById('billingPreview');
    const sameAsShipping  = document.getElementById('sameAsShipping');

    const shipField = document.getElementById('shippingAddressField');
    const shipPhone = document.getElementById('shippingPhoneField');
    const billField = document.getElementById('billingAddressField');

    const shippingRadios = document.querySelectorAll('input[name="shippingAddressId"]');
    const billingRadios  = document.querySelectorAll('input[name="billingAddressId"]');


    const initialBilling = document.querySelector('input[name="billingAddressId"]:checked') || null;

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

    function updateShippingFromRadio(radio) {
        if (!radio) return;
        toggleActive(radio);
        const txt = toText(radio);
        shippingPreview.textContent = txt;
        shipField.value = txt;
        shipPhone.value = radio.getAttribute('data-phone') || '';

        if (sameAsShipping && sameAsShipping.checked) {
            billingPreview.textContent = txt;
            billField.value = txt;
        }
    }

    function updateBillingFromRadio(radio) {
        if (!radio) return;
        toggleActive(radio);
        const txt = toText(radio);
        billingPreview.textContent = txt;
        billField.value = txt;
    }

    shippingRadios.forEach(r => {
        r.addEventListener('change', (e) => {
            updateShippingFromRadio(e.target);
        });
        const card = r.closest('.addr-card');
        card && card.addEventListener('click', () => r.click());
    });

    billingRadios.forEach(r => {
        r.addEventListener('change', (e) => {
            if (sameAsShipping && sameAsShipping.checked) return;
            updateBillingFromRadio(e.target);
        });
        const card = r.closest('.addr-card');
        card && card.addEventListener('click', () => r.click());
    });

    function applySameAsShippingState() {
        const disabled = sameAsShipping && sameAsShipping.checked;

        billingRadios.forEach(r => {
            r.disabled = disabled;
            r.required = !disabled;
            const card = r.closest('.addr-card');
            if (card && disabled) {
                card.classList.remove('active');
            }
        });

        if (disabled) {
            const sel = document.querySelector('input[name="shippingAddressId"]:checked');
            if (sel) {
                const txt = toText(sel);
                billingPreview.textContent = txt;
                billField.value = txt;
            } else {
                billingPreview.textContent = 'Same as shipping';
                billField.value = '';
            }
        } else {
            let billSel = document.querySelector('input[name="billingAddressId"]:checked');

            if (!billSel && initialBilling) {
                initialBilling.checked = true;
                billSel = initialBilling;
            }

            if (billSel) {
                updateBillingFromRadio(billSel);
            } else {
                billingPreview.textContent = 'Not selected';
                billField.value = '';
            }
        }
    }

    if (sameAsShipping) {
        sameAsShipping.addEventListener('change', applySameAsShippingState);
    }

    const initialShipping = document.querySelector('input[name="shippingAddressId"]:checked');
    if (initialShipping) {
        updateShippingFromRadio(initialShipping);
    } else {
        shippingPreview.textContent = 'Not selected';
        shipField.value = '';
        shipPhone.value = '';
    }

    if (!sameAsShipping || !sameAsShipping.checked) {
        if (initialBilling) {
            updateBillingFromRadio(initialBilling);
        } else {
            billingPreview.textContent = 'Not selected';
            billField.value = '';
        }
    }

    applySameAsShippingState();
});
