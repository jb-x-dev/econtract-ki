/**
 * eContract KI - Unified Menu System
 * Version: 5.0
 * 
 * Zentrales, einheitliches Menü für alle Seiten
 * Überarbeitete Struktur: Logische Gruppierung, keine Duplikate
 */

const UnifiedMenu = {
    // Menü-Struktur (überarbeitet)
    menuStructure: [
        {
            id: 'dashboard',
            icon: '📊',
            title: 'Dashboard',
            url: 'dashboard.html',
            description: 'Übersicht und Statistiken'
        },
        {
            id: 'contracts',
            icon: '📝',
            title: 'Verträge',
            url: null,
            description: 'Vertragsverwaltung',
            submenu: [
                { title: 'Alle Verträge', url: 'contracts.html', icon: '📋' },
                { title: 'Neuer Vertrag', url: 'contract-edit.html', icon: '➕' },
                { title: 'Rahmenverträge', url: 'framework-contracts.html', icon: '📑' },
                { title: 'Fristen & Termine', url: 'deadlines.html', icon: '⏰' },
                { title: 'Kalender', url: 'calendar.html', icon: '📅' }
            ]
        },
        {
            id: 'ai',
            icon: '🤖',
            title: 'KI-Funktionen',
            url: null,
            description: 'KI-gestützte Funktionen',
            badge: 'KI',
            submenu: [
                { title: 'KI-Assistent', url: 'ai-assistant.html', icon: '💬' },
                { title: 'Vertragsanalyse', url: 'contract-analysis.html', icon: '📊' },
                { title: 'Vertragserstellung', url: 'ai-contract-creation.html', icon: '✨' },
                { title: 'OCR & Import', url: 'contract-import.html', icon: '📤' },
                { title: 'OCR Scan', url: 'ocr-scan.html', icon: '📷' }
            ]
        },
        {
            id: 'reports',
            icon: '📈',
            title: 'Berichte',
            url: null,
            description: 'Auswertungen und Reports',
            submenu: [
                { title: 'Vertragsberichte', url: 'reports/contracts.html', icon: '📊' },
                { title: 'Finanzberichte', url: 'reports/financial.html', icon: '💰' },
                { title: 'Compliance-Berichte', url: 'reports/compliance.html', icon: '⚖️' },
                { title: 'Dashboard-Reports', url: 'reports/dashboard.html', icon: '📈' }
            ]
        },
        {
            id: 'workflows',
            icon: '✅',
            title: 'Workflows',
            url: null,
            description: 'Genehmigungsworkflows',
            submenu: [
                { title: 'Offene Genehmigungen', url: 'workflows.html', icon: '⏳' },
                { title: 'Genehmigte Verträge', url: 'approved-contracts.html', icon: '✅' },
                { title: 'Abgelehnte Verträge', url: 'rejected-contracts.html', icon: '❌' }
            ]
        },
        {
            id: 'masterdata',
            icon: '🔧',
            title: 'Stammdaten',
            url: null,
            description: 'Stammdaten pflegen',
            submenu: [
                { title: 'Partner', url: 'partners.html', icon: '🏢' },
                { title: 'Kategorien', url: 'categories.html', icon: '🏷️' },
                { title: 'Preise', url: 'price-management.html', icon: '💵' },
                { title: 'Vertragspflege', url: 'maintenance.html', icon: '🛠️' }
            ]
        },
        {
            id: 'admin',
            icon: '👥',
            title: 'Administration',
            url: null,
            description: 'Systemverwaltung',
            submenu: [
                { title: 'Benutzer', url: 'admin/users.html', icon: '👤' },
                { title: 'Rollen & Rechte', url: 'admin/roles.html', icon: '🎭' },
                { title: 'Gruppen', url: 'admin/groups.html', icon: '👥' },
                { title: 'Einstellungen', url: 'settings/general.html', icon: '⚙️' },
                { title: 'System', url: 'settings/system.html', icon: '🖥️' }
            ]
        },
        {
            id: 'help',
            icon: '❓',
            title: 'Hilfe',
            url: null,
            description: 'Hilfe und Support',
            submenu: [
                { title: 'Dokumentation', url: 'help/docs.html', icon: '📚' },
                { title: 'FAQ', url: 'help/faq.html', icon: '💡' },
                { title: 'Support', url: 'help/support.html', icon: '🆘' }
            ]
        }
    ],

    // Aktuellen Pfad ermitteln
    getCurrentPage() {
        const path = window.location.pathname;
        return path.substring(path.lastIndexOf('/') + 1) || 'dashboard.html';
    },

    // Menü rendern
    render() {
        const currentPage = this.getCurrentPage();
        const sidebar = document.getElementById('unified-sidebar');
        
        if (!sidebar) {
            console.error('Unified sidebar container not found');
            return;
        }

        let html = `
            <div class="sidebar-header">
                <div class="sidebar-logo">
                    <span class="sidebar-logo-icon">📄</span>
                    <h2 class="sidebar-logo-text">eContract KI</h2>
                </div>
                <button class="sidebar-toggle" onclick="UnifiedMenu.toggleSidebar()">
                    <span class="toggle-icon">☰</span>
                </button>
            </div>
            <div class="sidebar-search">
                <input type="text" placeholder="Suche..." class="sidebar-search-input" id="menuSearch" onkeyup="UnifiedMenu.searchMenu()">
                <span class="search-icon">🔍</span>
            </div>
            <nav class="sidebar-nav">
        `;

        this.menuStructure.forEach(item => {
            const isActive = item.url === currentPage || 
                            (item.submenu && item.submenu.some(sub => sub.url === currentPage));
            const hasSubmenu = item.submenu && item.submenu.length > 0;
            
            html += `
                <div class="sidebar-item ${isActive ? 'active' : ''} ${hasSubmenu ? 'has-submenu' : ''}" data-id="${item.id}">
                    <a href="${item.url || '#'}" class="sidebar-link ${!item.url ? 'submenu-trigger' : ''}" 
                       ${!item.url ? `onclick="UnifiedMenu.toggleSubmenu('${item.id}'); return false;"` : ''}>
                        <span class="sidebar-icon">${item.icon}</span>
                        <span class="sidebar-title">${item.title}</span>
                        ${item.badge ? `<span class="sidebar-badge sidebar-badge-${item.badge.toLowerCase()}">${item.badge}</span>` : ''}
                        ${hasSubmenu ? '<span class="submenu-arrow">▼</span>' : ''}
                    </a>
            `;

            if (hasSubmenu) {
                html += `<div class="sidebar-submenu ${isActive ? 'open' : ''}">`;
                item.submenu.forEach(subitem => {
                    const isSubActive = subitem.url === currentPage;
                    html += `
                        <a href="${subitem.url}" class="sidebar-sublink ${isSubActive ? 'active' : ''}">
                            <span class="sidebar-subicon">${subitem.icon}</span>
                            <span class="sidebar-subtitle">${subitem.title}</span>
                        </a>
                    `;
                });
                html += `</div>`;
            }

            html += `</div>`;
        });

        html += `
            </nav>
            <div class="sidebar-footer">
                <a href="profile.html" class="sidebar-link">
                    <span class="sidebar-icon">👤</span>
                    <span class="sidebar-title">Profil</span>
                </a>
                <a href="/econtract/swagger-ui.html" target="_blank" class="sidebar-link">
                    <span class="sidebar-icon">📚</span>
                    <span class="sidebar-title">API Dokumentation</span>
                </a>
                <form action="/econtract/logout" method="post" style="margin: 0;">
                    <button type="submit" class="sidebar-link" style="width: 100%; text-align: left; background: none; border: none; cursor: pointer; padding: 0.75rem 1rem; color: inherit; font-size: inherit;">
                        <span class="sidebar-icon">🚪</span>
                        <span class="sidebar-title">Abmelden</span>
                    </button>
                </form>
                <div class="sidebar-version">Version 5.0</div>
            </div>
        `;

        sidebar.innerHTML = html;
    },

    // Submenu toggle
    toggleSubmenu(itemId) {
        const item = document.querySelector(`.sidebar-item[data-id="${itemId}"]`);
        if (!item) return;

        const submenu = item.querySelector('.sidebar-submenu');
        const arrow = item.querySelector('.submenu-arrow');
        
        if (submenu) {
            const isOpen = submenu.classList.contains('open');
            
            // Alle anderen Submenus schließen
            document.querySelectorAll('.sidebar-submenu.open').forEach(sm => {
                if (sm !== submenu) {
                    sm.classList.remove('open');
                    const otherArrow = sm.parentElement.querySelector('.submenu-arrow');
                    if (otherArrow) otherArrow.style.transform = 'rotate(0deg)';
                }
            });

            // Aktuelles Submenu togglen
            submenu.classList.toggle('open');
            if (arrow) {
                arrow.style.transform = isOpen ? 'rotate(0deg)' : 'rotate(180deg)';
            }
        }
    },
    
    // Setup event listeners after menu is rendered
    setupEventListeners() {
        // Submenu triggers
        document.querySelectorAll('.submenu-trigger').forEach(trigger => {
            trigger.addEventListener('click', (e) => {
                e.preventDefault();
                const itemId = trigger.closest('.sidebar-item').dataset.id;
                UnifiedMenu.toggleSubmenu(itemId);
            });
        });
    },

    // Sidebar toggle (mobile)
    toggleSidebar() {
        const sidebar = document.getElementById('unified-sidebar');
        if (sidebar) {
            sidebar.classList.toggle('collapsed');
            document.body.classList.toggle('sidebar-collapsed');
        }
    },

    // Menü-Suche
    searchMenu() {
        const searchTerm = document.getElementById('menuSearch').value.toLowerCase();
        const items = document.querySelectorAll('.sidebar-item');

        items.forEach(item => {
            const title = item.querySelector('.sidebar-title').textContent.toLowerCase();
            const sublinks = item.querySelectorAll('.sidebar-subtitle');
            let hasMatch = title.includes(searchTerm);

            sublinks.forEach(sublink => {
                if (sublink.textContent.toLowerCase().includes(searchTerm)) {
                    hasMatch = true;
                }
            });

            item.style.display = hasMatch || searchTerm === '' ? 'block' : 'none';
        });
    },

    // Initialisierung
    init() {
        // Warten bis DOM geladen ist
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                this.removeOldMenu();
                this.render();
                this.setupEventListeners();
            });
        } else {
            this.removeOldMenu();
            this.render();
            this.setupEventListeners();
        }
    },

    // Entferne altes Menü
    removeOldMenu() {
        // Entferne alle header-Elemente außerhalb von #main-content
        document.querySelectorAll('body > header, body > nav, .main-navigation').forEach(el => {
            console.log('Removing old menu element:', el.tagName, el.className);
            el.remove();
        });
        
        // Minimiere den blauen Header-Bereich - AGGRESSIV
        setTimeout(() => {
            // Entferne Breadcrumbs
            document.querySelectorAll('.breadcrumbs, .page-breadcrumbs').forEach(el => el.remove());
            
            // Entferne Quick Actions
            document.querySelectorAll('.quick-actions-bar, .action-bar, .page-actions').forEach(el => el.remove());
            
            // Finde ALLE Elemente mit blauem Hintergrund und prüfe sie
            document.querySelectorAll('*').forEach(el => {
                const style = window.getComputedStyle(el);
                const bg = style.backgroundColor;
                
                // Prüfe auf blauen Hintergrund (verschiedene Blautöne)
                if (bg.includes('25, 118, 210') || bg.includes('33, 150, 243') || 
                    bg.includes('30, 136, 229') || bg.includes('21, 101, 192')) {
                    
                    // Wenn es Buttons enthält, ist es wahrscheinlich das Banner
                    const buttons = el.querySelectorAll('button');
                    if (buttons.length > 2) {
                        console.log('Found blue banner with', buttons.length, 'buttons - REMOVING');
                        el.remove();
                    }
                }
            });
        }, 200);
        
        // Zweiter Versuch nach längerem Timeout
        setTimeout(() => {
            document.querySelectorAll('*').forEach(el => {
                if (el.textContent.includes('eContract KI') && 
                    el.textContent.includes('Intelligente Vertragsverwaltung') &&
                    el.querySelectorAll('button').length > 3) {
                    console.log('Found banner by content - REMOVING');
                    el.remove();
                }
            });
        }, 500);
    }
};

// Erstelle Sidebar-Container SOFORT (vor DOMContentLoaded)
// um navigation.js zu signalisieren, dass unified menu aktiv ist
if (!document.getElementById('unified-sidebar')) {
    const sidebar = document.createElement('aside');
    sidebar.id = 'unified-sidebar';
    document.body.insertBefore(sidebar, document.body.firstChild);
}

// Auto-Initialisierung
UnifiedMenu.init();
