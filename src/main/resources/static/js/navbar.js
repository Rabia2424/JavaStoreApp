(function(){
    const navForm = document.getElementById('navSearchForm');
    if (!navForm) return;

    const searchInput = navForm.querySelector('input[name="q"]');
    const action = navForm.getAttribute('action') || '/products/list';


    const ALLOWED = new Set([
        'q','category_id','minPrice','maxPrice','sortBy','direction','size','page_no'
    ]);

    navForm.addEventListener('submit', function(e){
        e.preventDefault();

        const cur = new URLSearchParams(window.location.search);
        const out = new URLSearchParams();

        for (const [k,v] of cur.entries()){
            if (ALLOWED.has(k)) out.set(k, v);
        }

        const q = (searchInput?.value || '').trim();
        if (q) out.set('q', q); else out.delete('q');
        out.set('page_no', '0');

        const url = action + (out.toString() ? ('?' + out.toString()) : '');
        window.location.assign(url);

    });
})();