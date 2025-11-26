document.addEventListener('DOMContentLoaded', function () {

    console.log('account.js DOM ready');

    const links = document.querySelectorAll('.account-nav .account-link');
    const content = document.getElementById('accountContent');

    console.log('account.js: links =', links.length, 'content =', !!content);

    if (!links.length || !content) {
        console.warn('account.js: links or content not found');
        return;
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
                })
                .catch(err => {
                    console.error(err);
                    content.innerHTML =
                        '<div class="empty-state">Something went wrong. Please try again.</div>';
                });
        });
    });

});
