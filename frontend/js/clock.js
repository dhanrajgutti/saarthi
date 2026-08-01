/**
 * LiveClock — Real-time date and time display for Bharat RTO Portal
 * Updates every 30 seconds to keep display accurate without excessive CPU usage.
 */
(function () {
    'use strict';

    var MONTHS = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];
    var DAYS = [
        'Sunday', 'Monday', 'Tuesday', 'Wednesday',
        'Thursday', 'Friday', 'Saturday'
    ];

    function pad(n) {
        return n < 10 ? '0' + n : '' + n;
    }

    function update() {
        var now = new Date();
        var dateEl = document.getElementById('liveDate');
        var timeEl = document.getElementById('liveTime');

        if (dateEl) {
            dateEl.textContent = now.getDate() + ' ' + MONTHS[now.getMonth()] + ' ' + now.getFullYear();
        }

        if (timeEl) {
            var hours = now.getHours();
            var ampm = hours >= 12 ? 'PM' : 'AM';
            var h12 = hours % 12;
            if (h12 === 0) h12 = 12;
            timeEl.textContent = DAYS[now.getDay()] + ', ' + h12 + ':' + pad(now.getMinutes()) + ' ' + ampm;
        }
    }

    function init() {
        update();
        setInterval(update, 30000);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
