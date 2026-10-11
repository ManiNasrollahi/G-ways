(function () {
    'use strict';
    var toggle = document.getElementById('menu-toggle');
    var sidebar = document.getElementById('application-sidebar');
    if (!toggle || !sidebar) return;
    var menu = document.getElementById('pageMenu');
    var menuStateKey = 'gateway-control-plane-menu-open';

    function setOpen(open) {
        menu.classList.toggle('is-collapsed', !open);
        sidebar.inert = !open;
        sidebar.setAttribute('aria-hidden', String(!open));
        toggle.setAttribute('aria-expanded', String(open));
        toggle.setAttribute('aria-label', open ? 'Close navigation menu' : 'Open navigation menu');
        window.localStorage.setItem(menuStateKey, String(open));
    }

    var savedState = window.localStorage.getItem(menuStateKey);
    setOpen(savedState === null ? true : savedState === 'true');
    toggle.addEventListener('click', function () { setOpen(toggle.getAttribute('aria-expanded') !== 'true'); });
    sidebar.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') { setOpen(false); toggle.focus(); }
    });
}());
