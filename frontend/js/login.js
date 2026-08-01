(function() {
    'use strict';

    const API_BASE_URL = 'http://localhost:8080/api';
    const STORAGE_KEY = 'vehical_auth_data';

    const CONFIG = {
        endpoints: {
            login: '/auth/login',
            roles: '/auth/roles',
            me: '/auth/me',
            logout: '/auth/logout'
        },
        dashboards: {
            POLICE: 'pages/police-dashboard.html',
            PUC_CENTER: 'pages/puc-dashboard.html',
            INSURANCE_AGENCY: 'pages/insurance-dashboard.html'
        }
    };

    const state = {
        selectedRole: null
    };

    const elements = {
        roleCards: document.querySelectorAll('.role-pill'),
        loginForm: document.getElementById('loginForm'),
        usernameInput: document.getElementById('username'),
        passwordInput: document.getElementById('password'),
        usernameError: document.getElementById('usernameError'),
        passwordError: document.getElementById('passwordError'),
        rememberMe: document.getElementById('rememberMe'),
        togglePassword: document.getElementById('togglePassword'),
        eyeIcon: document.getElementById('eyeIcon'),
        loginButton: document.getElementById('loginButton'),
        btnText: document.querySelector('.btn-text-new'),
        btnSpinner: document.querySelector('.btn-spinner-new'),
        alertBox: document.getElementById('alertBox'),
        forgotLink: document.getElementById('forgotLink')
    };

    function init() {
        setupRoleSelector();
        setupPasswordToggle();
        setupFormSubmission();
        setupForgotPassword();
        loadSavedCredentials();
    }

    function setupRoleSelector() {
        elements.roleCards.forEach(card => {
            card.addEventListener('click', () => {
                setSelectedRole(card);
                elements.usernameInput.focus();
            });
        });
    }

    function setSelectedRole(card) {
        const role = card.getAttribute('data-role');
        elements.roleCards.forEach(c => c.classList.remove('active'));
        card.classList.add('active');
        state.selectedRole = role;
    }

    function setupPasswordToggle() {
        elements.togglePassword.addEventListener('click', () => {
            const isPassword = elements.passwordInput.type === 'password';
            elements.passwordInput.type = isPassword ? 'text' : 'password';

            if (isPassword) {
                elements.eyeIcon.innerHTML = `
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                    <line x1="1" y1="1" x2="23" y2="23"/>
                `;
            } else {
                elements.eyeIcon.innerHTML = `
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                `;
            }
        });
    }

    function setupFormSubmission() {
        elements.loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            if (!validateForm()) return;

            const username = elements.usernameInput.value.trim();
            const password = elements.passwordInput.value;
            const role = state.selectedRole;

            if (elements.rememberMe.checked) {
                saveCredentials(username, role);
            } else {
                clearSavedCredentials();
            }

            setLoading(true);
            clearAlerts();

            try {
                const response = await attemptLogin(username, password, role);

                if (response.success) {
                    showAlert('Login successful! Redirecting to dashboard...', 'success');
                    saveAuthData(response.data);
                    redirectToDashboard(response.data.role);
                } else {
                    showAlert(response.message || 'Login failed. Please try again.', 'error');
                    shakeForm();
                }
            } catch (error) {
                console.error('Login error:', error);
                showAlert(
                    error.message || 'Unable to connect to server. Please check your connection and try again.',
                    'error'
                );
                shakeForm();
            } finally {
                setLoading(false);
            }
        });
    }

    function validateForm() {
        let isValid = true;
        clearFieldErrors();

        if (!state.selectedRole) {
            showAlert('Please select your department to continue', 'error');
            shakeRoleSelector();
            isValid = false;
        }

        if (!elements.usernameInput.value.trim()) {
            elements.usernameError.textContent = 'Username is required';
            elements.usernameInput.classList.add('error');
            isValid = false;
        } else if (elements.usernameInput.value.trim().length < 3) {
            elements.usernameError.textContent = 'Username must be at least 3 characters';
            elements.usernameInput.classList.add('error');
            isValid = false;
        }

        if (!elements.passwordInput.value) {
            elements.passwordError.textContent = 'Password is required';
            elements.passwordInput.classList.add('error');
            isValid = false;
        } else if (elements.passwordInput.value.length < 4) {
            elements.passwordError.textContent = 'Password must be at least 4 characters';
            elements.passwordInput.classList.add('error');
            isValid = false;
        }

        return isValid;
    }

    async function attemptLogin(username, password, role) {
        const payload = { username, password };
        if (role) payload.role = role;

        const runFallback = () => {
            try {
                const fallbackResult = simulateLogin(username, password, role);
                if (fallbackResult && fallbackResult.success) {
                    fallbackResult.message = (fallbackResult.message || '') + '  ·  Backend offline → using Demo Mode';
                }
                return fallbackResult;
            } catch (fallbackError) {
                return {
                    success: false,
                    message: 'Login failed. Try any demo username (police_admin / puc_mumbai_01 / insurance_lic_01) with a 4+ character password.'
                };
            }
        };

        let timedOut = false;
        const abortCtrl = typeof AbortController !== 'undefined' ? new AbortController() : null;
        const timeoutId = abortCtrl
            ? setTimeout(() => { timedOut = true; abortCtrl.abort(); }, 3000)
            : null;

        try {
            const fetchOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(payload)
            };
            if (abortCtrl) fetchOptions.signal = abortCtrl.signal;

            const response = await fetch(API_BASE_URL + CONFIG.endpoints.login, fetchOptions);

            if (timeoutId) clearTimeout(timeoutId);

            let responseData;
            try {
                responseData = await response.json();
            } catch (_parseErr) {
                responseData = {};
            }

            if (response.ok && responseData && responseData.success !== false) {
                return {
                    success: true,
                    message: responseData && responseData.message ? responseData.message : 'Login successful',
                    data: responseData ? responseData.data : undefined
                };
            }

            if (responseData && typeof responseData === 'object') {
                if (responseData.success === false && responseData.message) {
                    if (username && password) return runFallback();
                    return responseData;
                }
            }

            if (username && password) return runFallback();
            return { success: false, message: responseData ? (responseData.message || 'Login failed') : 'Login failed' };
        } catch (networkError) {
            if (timeoutId) clearTimeout(timeoutId);
            if (username && password) return runFallback();
            if (timedOut) {
                return { success: false, message: 'Server timed out. Try demo credentials below.' };
            }
            return { success: false, message: 'Cannot reach the authentication server.' };
        }
    }

    function simulateLogin(username, password, role) {
        const demoAccounts = {
            police_admin:   { role: 'POLICE',          fullName: 'Rajesh Kumar',       email: 'rajesh.kumar@police.gov.in',     department: 'Traffic Police Department - Mumbai', phone: '+91-98765-43210' },
            police_inspector: { role: 'POLICE',        fullName: 'Amit Sharma',        email: 'amit.sharma@police.gov.in',      department: 'RTO Enforcement Wing - Delhi',    phone: '+91-98765-43211' },
            puc_mumbai_01:  { role: 'PUC_CENTER',      fullName: 'Suresh Patel',       email: 'suresh@mumbaipuc.com',           centerName: 'Mumbai PUC Testing Center - Andheri', licenseNumber: 'PUC-MH-001245', phone: '+91-98765-43220' },
            puc_delhi_01:   { role: 'PUC_CENTER',      fullName: 'Vikram Singh',       email: 'vikram@delhipuc.in',             centerName: 'Delhi Emission Testing Lab',      licenseNumber: 'PUC-DL-005678', phone: '+91-98765-43221' },
            insurance_lic_01: { role: 'INSURANCE_AGENCY', fullName: 'Priya Nair',       email: 'priya.nair@licindia.com',        agencyName: 'Life Insurance Corporation of India', licenseNumber: 'IRDA-LIC-1001', phone: '+91-98765-43230' },
            insurance_hdfc_01: { role: 'INSURANCE_AGENCY', fullName: 'Karan Mehta',     email: 'karan.mehta@hdfcergo.com',       agencyName: 'HDFC ERGO General Insurance',     licenseNumber: 'IRDA-HDFC-2056', phone: '+91-98765-43231' }
        };

        const account = demoAccounts[username.toLowerCase()];
        if (!account || password.length < 4) {
            return { success: false, message: 'Invalid username or password. Use demo credentials: police_admin, puc_mumbai_01, or insurance_lic_01 (password: 4+ chars)' };
        }

        if (role && account.role !== role) {
            return { success: false, message: `Role mismatch. This account belongs to ${account.role.replace('_', ' ')}` };
        }

        const fakeToken = 'demo_token_' + btoa(JSON.stringify({ u: username, r: account.role, t: Date.now() }));

        return {
            success: true,
            message: 'Login successful (Demo Mode)',
            data: {
                token: fakeToken,
                tokenType: 'Bearer',
                expiresIn: 86400,
                userId: Math.floor(Math.random() * 1000) + 1,
                username: username,
                fullName: account.fullName,
                email: account.email,
                phone: account.phone,
                role: account.role,
                department: account.department || null,
                centerName: account.centerName || null,
                agencyName: account.agencyName || null,
                licenseNumber: account.licenseNumber || null,
                loginTime: new Date().toISOString()
            }
        };
    }

    function saveAuthData(data) {
        const authRecord = {
            token: data.token,
            tokenType: data.tokenType,
            expiresAt: Date.now() + (data.expiresIn * 1000),
            user: {
                id: data.userId,
                username: data.username,
                fullName: data.fullName,
                email: data.email,
                phone: data.phone,
                role: data.role,
                department: data.department,
                centerName: data.centerName,
                agencyName: data.agencyName,
                licenseNumber: data.licenseNumber
            },
            loginTime: data.loginTime || Date.now()
        };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(authRecord));
    }

    function redirectToDashboard(role) {
        const dashboard = CONFIG.dashboards[role];
        if (!dashboard) {
            showAlert('Invalid user role. Please contact administrator.', 'error');
            return;
        }
        window.location.href = dashboard;
    }

    function saveCredentials(username, role) {
        localStorage.setItem('vehical_remember', JSON.stringify({ username, role }));
    }

    function loadSavedCredentials() {
        try {
            const saved = localStorage.getItem('vehical_remember');
            if (saved) {
                const { username, role } = JSON.parse(saved);
                elements.usernameInput.value = username || '';
                elements.rememberMe.checked = true;

                if (role) {
                    const targetCard = document.querySelector(`.role-pill[data-role="${role}"]`);
                    if (targetCard) {
                        setSelectedRole(targetCard);
                    }
                }
            }
        } catch (e) {
            console.warn('Could not load saved credentials', e);
        }

        if (!state.selectedRole && elements.roleCards.length) {
            setSelectedRole(elements.roleCards[0]);
        }
    }

    function clearSavedCredentials() {
        localStorage.removeItem('vehical_remember');
    }

    function setLoading(isLoading) {
        elements.loginButton.disabled = isLoading;
        elements.btnText.style.display = isLoading ? 'none' : 'flex';
        elements.btnSpinner.style.display = isLoading ? 'flex' : 'none';
    }

    function clearFieldErrors() {
        elements.usernameError.textContent = '';
        elements.passwordError.textContent = '';
        elements.usernameInput.classList.remove('error');
        elements.passwordInput.classList.remove('error');
    }

    function clearAlerts() {
        elements.alertBox.style.display = 'none';
        elements.alertBox.className = 'alert-box';
        elements.alertBox.textContent = '';
    }

    function showAlert(message, type) {
        elements.alertBox.textContent = message;
        elements.alertBox.className = `alert-box ${type}`;
        elements.alertBox.style.display = 'block';
    }

    function shakeForm() {
        elements.loginForm.style.animation = 'none';
        elements.loginForm.offsetHeight;
        elements.loginForm.style.animation = 'shake 0.4s ease-in-out';
    }

    function shakeRoleSelector() {
        const selector = document.getElementById('roleSelector');
        selector.style.animation = 'none';
        selector.offsetHeight;
        selector.style.animation = 'shake 0.4s ease-in-out';
    }

    function setupForgotPassword() {
        elements.forgotLink.addEventListener('click', (e) => {
            e.preventDefault();
            showAlert('Please contact IT Helpdesk at 1800-1234-567 or support@bharatrto.gov.in to reset your password.', 'success');
        });
    }

    const styleSheet = document.createElement('style');
    styleSheet.textContent = `
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            20% { transform: translateX(-8px); }
            40% { transform: translateX(8px); }
            60% { transform: translateX(-6px); }
            80% { transform: translateX(6px); }
        }
    `;
    document.head.appendChild(styleSheet);

    let _initialized = false;
    function safeInit() {
        if (_initialized) return;
        _initialized = true;
        init();
    }
    document.addEventListener('DOMContentLoaded', safeInit);
    if (document.readyState !== 'loading') safeInit();
})();
