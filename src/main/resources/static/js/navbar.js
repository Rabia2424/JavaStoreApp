(function () {
    const navForm    = document.getElementById('navSearchForm');
    if (!navForm) return;

    const searchInput = navForm.querySelector('input[name="q"]');
    const filterForm  = document.getElementById('filter-form');
    const collapse    = document.querySelector('.navbar .navbar-collapse');

    const urlQ = new URLSearchParams(location.search).get('q');
    if (urlQ && searchInput) searchInput.value = urlQ;

    navForm.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') e.preventDefault();
    });

    function triggerSearch() {
        if (!(filterForm && typeof window.applyFilters === 'function')) return;

        const q = (searchInput?.value || '').trim();

        let qEl = filterForm.elements.namedItem('q');
        if (qEl) qEl.value = q;

        const pg = filterForm.elements.namedItem('page_no');
        if (pg) pg.value = '0';

        const qs = new URLSearchParams(new FormData(filterForm)).toString();
        history.replaceState(null, '', location.pathname + (qs ? '?' + qs : ''));

        window.applyFilters(false, filterForm);

        collapse?.classList.add('show');
    }

    let t;
    searchInput?.addEventListener('input', () => {
        clearTimeout(t);
        t = setTimeout(triggerSearch, 300);
    });

    clearBtn?.addEventListener('click', () => {
        if (!searchInput) return;
        searchInput.value = '';

        const cur = new URLSearchParams(location.search);
        cur.delete('q');
        history.replaceState(null, '', location.pathname + (cur.toString() ? '?' + cur.toString() : ''));

        const qEl = filterForm?.elements?.namedItem('q');
        if (qEl) qEl.value = '';
        const pg = filterForm?.elements?.namedItem('page_no');
        if (pg) pg.value = '0';

        triggerSearch();
        searchInput.focus();
    });
})();
