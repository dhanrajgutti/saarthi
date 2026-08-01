/**
 * DronaSystems RTO Portal — Main UI Script
 * Handles: dark mode toggle, header scroll effects, input focus states,
 * and smooth entrance animations.
 */
(function () {
    'use strict';

    /* ---------- Dark Mode Toggle ---------- */
    var DARK_KEY = 'rto_dark_mode';

    function applyDarkMode(isDark) {
        document.documentElement.classList.toggle('dark-mode', isDark);
        var btn = document.getElementById('darkToggle');
        if (btn) {
            btn.setAttribute('aria-pressed', isDark ? 'true' : 'false');
            // Swap icon: moon → sun
            btn.innerHTML = isDark
                ? '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>'
                : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>';
        }
    }

    function initDarkMode() {
        var saved = localStorage.getItem(DARK_KEY);
        var prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
        var isDark = saved !== null ? saved === 'true' : prefersDark;
        applyDarkMode(isDark);

        var btn = document.getElementById('darkToggle');
        if (btn) {
            btn.addEventListener('click', function () {
                var current = document.documentElement.classList.contains('dark-mode');
                applyDarkMode(!current);
                localStorage.setItem(DARK_KEY, (!current).toString());
            });
        }
    }

    /* ---------- Header Scroll Shadow ---------- */
    function initHeaderScroll() {
        var header = document.getElementById('siteHeader');
        if (!header) return;

        var ticking = false;
        window.addEventListener('scroll', function () {
            if (!ticking) {
                requestAnimationFrame(function () {
                    header.classList.toggle('header-scrolled', window.scrollY > 10);
                    ticking = false;
                });
                ticking = true;
            }
        }, { passive: true });
    }

    /* ---------- Input Focus Animation ---------- */
    function initInputEffects() {
        var inputs = document.querySelectorAll('.input-wrap input');
        inputs.forEach(function (input) {
            input.addEventListener('focus', function () {
                this.closest('.form-field').classList.add('field-focused');
            });
            input.addEventListener('blur', function () {
                this.closest('.form-field').classList.remove('field-focused');
            });
        });
    }

    /* ---------- Staggered Card Entrance ---------- */
    function initEntrance() {
        var cards = document.querySelectorAll('.fade-in-up, .fade-in-down');
        cards.forEach(function (el, i) {
            el.style.animationDelay = (i * 0.1) + 's';
        });
    }

    /* ---------- Init All ---------- */
    function init() {
        initDarkMode();
        initHeaderScroll();
        initInputEffects();
        initEntrance();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
