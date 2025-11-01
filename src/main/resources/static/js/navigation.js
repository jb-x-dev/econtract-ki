/**
 * eContract KI - Vollständige Navigation
 * Version 2.1
 */

// Menüstruktur mit allen Modulen
const menuStructure = {
    main: [
        {
            id: 'dashboard',
            title: 'Dashboard',
            icon: '📊',
            url: 'dashboard.html',
            description: 'Übersicht und Statistiken'
        },
        {
            id: 'contracts',
            title: 'Verträge',
            icon: '📝',
            url: 'contracts.html',
            description: 'Vertragsverwaltung',
            submenu: [
                { title: 'Alle Verträge', url: 'contracts.html' },
                { title: 'Neuer Vertrag', url: 'contract-edit.html?mode=new' },
                { title: 'Vorlagen', url: 'contract-templates.html' },
                { title: 'Archiv', url: 'contracts.html?status=archived' }
            ]
        },
        {
            id: 'import',
            title: 'Import & OCR',
            icon: '📥',
            url: 'contract-import.html',
            description: 'Intelligenter Vertragsimport',
            badge: 'KI',
            submenu: [
                { title: 'Datei hochladen', url: 'contract-import.html' },
                { title: 'OCR-Erkennung', url: 'ocr-scan.html', badge: 'NEU' },
                { title: 'Batch-Import', url: 'contract-import.html?tab=batch' },
                { title: 'Arbeitsvorrat', url: 'contract-import.html?tab=queue' },
                { title: 'Import-Historie', url: 'contract-import.html?tab=history' }
            ]
        },
        {
            id: 'ai',
            title: 'KI-Assistent',
            icon: '🤖',
            url: 'ai-assistant.html',
            description: 'KI-gestützte Funktionen',
            badge: 'KI',
            submenu: [
                { title: 'Vertrag generieren', url: 'ai-assistant.html?tab=generate' },
                { title: 'Risiken analysieren', url: 'ai-assistant.html?tab=analyze' },
                { title: 'Klauseln vorschlagen', url: 'ai-assistant.html?tab=clauses' },
                { title: 'Vertrag zusammenfassen', url: 'ai-summary.html', badge: 'NEU' },
                { title: 'Vertrag vergleichen', url: 'ai-compare.html', badge: 'NEU' }
            ]
        },
        {
            id: 'framework',
            title: 'Rahmenverträge',
            icon: '📑',
            url: 'framework-contracts.html',
            description: 'Rahmenverträge verwalten',
            submenu: [
                { title: 'Alle Rahmenverträge', url: 'framework-contracts.html' },
                { title: 'Neuer Rahmenvertrag', url: 'framework-edit.html?mode=new' },
                { title: 'Volumen-Tracking', url: 'framework-volume.html' }
            ]
        },
        {
            id: 'workflows',
            title: 'Genehmigungen',
            icon: '✅',
            url: 'workflows.html',
            description: 'Workflow-Management',
            submenu: [
                { title: 'Meine Aufgaben', url: 'workflows.html?filter=my' },
                { title: 'Alle Workflows', url: 'workflows.html' },
                { title: 'Workflow-Designer', url: 'workflow-designer.html', badge: 'PRO' }
            ]
        },
        {
            id: 'deadlines',
            title: 'Fristen',
            icon: '⏰',
            url: 'deadlines.html',
            description: 'Fristenmanagement',
            submenu: [
                { title: 'Alle Fristen', url: 'deadlines.html' },
                { title: 'Kritische Fristen', url: 'deadlines.html?filter=critical' },
                { title: 'Kündigungsfristen', url: 'deadlines.html?type=cancellation' },
                { title: 'Verlängerungen', url: 'deadlines.html?type=renewal' }
            ]
        },
        {
            id: 'calendar',
            title: 'Kalender',
            icon: '📅',
            url: 'calendar.html',
            description: 'Terminübersicht'
        },
        {
            id: 'maintenance',
            title: 'Pflege',
            icon: '🔧',
            url: 'maintenance.html',
            description: 'Vertragspflege',
            submenu: [
                { title: 'Wartungsaufgaben', url: 'maintenance.html' },
                { title: 'Checklisten', url: 'maintenance-checklists.html' },
                { title: 'Prüfungen', url: 'maintenance-audits.html' }
            ]
        },
        {
            id: 'reports',
            title: 'Berichte',
            icon: '📊',
            url: 'reports.html',
            description: 'Reporting & Analytics',
            badge: 'NEU',
            submenu: [
                { title: 'Standard-Berichte', url: 'reports.html' },
                { title: 'Benutzerdefiniert', url: 'reports-custom.html' },
                { title: 'Export (PDF/Excel)', url: 'reports-export.html' },
                { title: 'Dashboards', url: 'dashboard.html' }
            ]
        }
    ],
    admin: [
        {
            id: 'users',
            title: 'Benutzer',
            icon: '👥',
            url: 'users.html',
            description: 'Benutzerverwaltung',
            submenu: [
                { title: 'Alle Benutzer', url: 'users.html' },
                { title: 'Rollen & Rechte', url: 'roles.html' },
                { title: 'Gruppen', url: 'groups.html' }
            ]
        },
        {
            id: 'settings',
            title: 'Einstellungen',
            icon: '⚙️',
            url: 'settings.html',
            description: 'Systemeinstellungen',
            submenu: [
                { title: 'Allgemein', url: 'settings.html' },
                { title: 'E-Mail', url: 'settings-email.html' },
                { title: 'KI-Konfiguration', url: 'settings-ai.html' },
                { title: 'OCR-Einstellungen', url: 'settings-ocr.html' },
                { title: 'Vorlagen', url: 'settings-templates.html' }
            ]
        },
        {
            id: 'api',
            title: 'API-Dokumentation',
            icon: '📚',
            url: '/econtract/swagger-ui.html',
            description: 'REST API',
            external: true
        }
    ],
    user: [
        {
            id: 'profile',
            title: 'Mein Profil',
            icon: '👤',
            url: 'profile.html'
        },
        {
            id: 'notifications',
            title: 'Benachrichtigungen',
            icon: '🔔',
            url: 'notifications.html',
            badge: '3'
        },
        {
            id: 'help',
            title: 'Hilfe',
            icon: '❓',
            url: 'help.html',
            submenu: [
                { title: 'Dokumentation', url: 'help.html' },
                { title: 'Video-Tutorials', url: 'tutorials.html' },
                { title: 'Support', url: 'support.html' }
            ]
        },
        {
            id: 'logout',
            title: 'Abmelden',
            icon: '🚪',
            url: 'logout.html'
        }
    ]
};

// Navigation HTML generieren
function generateNavigation() {
    const nav = document.createElement('nav');
    nav.className = 'main-navigation';
    nav.innerHTML = `
        <div class="nav-header">
            <div class="nav-logo">
                <span class="logo-icon">📋</span>
                <span class="logo-text">eContract KI</span>
            </div>
            <button class="nav-toggle" onclick="toggleNavigation()">
                <span class="hamburger"></span>
            </button>
        </div>
        
        <div class="nav-search">
            <input type="text" placeholder="Suche..." class="nav-search-input" onkeyup="searchMenu(this.value)">
            <span class="search-icon">🔍</span>
        </div>
        
        <div class="nav-sections">
            <div class="nav-section">
                <div class="nav-section-title">Hauptmenü</div>
                ${generateMenuItems(menuStructure.main)}
            </div>
            
            <div class="nav-section">
                <div class="nav-section-title">Administration</div>
                ${generateMenuItems(menuStructure.admin)}
            </div>
            
            <div class="nav-section nav-section-user">
                ${generateMenuItems(menuStructure.user)}
            </div>
        </div>
        
        <div class="nav-footer">
            <div class="nav-user-info">
                <div class="user-avatar">👤</div>
                <div class="user-details">
                    <div class="user-name">Max Mustermann</div>
                    <div class="user-role">Administrator</div>
                </div>
            </div>
        </div>
    `;
    
    return nav;
}

// Menü-Items generieren
function generateMenuItems(items) {
    return items.map(item => {
        const hasSubmenu = item.submenu && item.submenu.length > 0;
        const isActive = window.location.pathname.includes(item.url);
        const badge = item.badge ? `<span class="nav-badge">${item.badge}</span>` : '';
        
        return `
            <div class="nav-item ${isActive ? 'active' : ''} ${hasSubmenu ? 'has-submenu' : ''}" 
                 data-item-id="${item.id}">
                <a href="${item.url}" class="nav-link" ${item.external ? 'target="_blank"' : ''}>
                    <span class="nav-icon">${item.icon}</span>
                    <span class="nav-title">${item.title}</span>
                    ${badge}
                    ${hasSubmenu ? '<span class="nav-arrow">▼</span>' : ''}
                </a>
                ${hasSubmenu ? `
                    <div class="nav-submenu">
                        ${item.submenu.map(sub => `
                            <a href="${sub.url}" class="nav-submenu-item">
                                ${sub.title}
                                ${sub.badge ? `<span class="nav-badge-sm">${sub.badge}</span>` : ''}
                            </a>
                        `).join('')}
                    </div>
                ` : ''}
            </div>
        `;
    }).join('');
}

// Navigation toggle
function toggleNavigation() {
    const nav = document.querySelector('.main-navigation');
    nav.classList.toggle('collapsed');
    localStorage.setItem('navCollapsed', nav.classList.contains('collapsed'));
}

// Submenu toggle
function toggleSubmenu(itemId) {
    const item = document.querySelector(`[data-item-id="${itemId}"]`);
    item.classList.toggle('submenu-open');
}

// Suche im Menü
function searchMenu(query) {
    const items = document.querySelectorAll('.nav-item');
    const lowerQuery = query.toLowerCase();
    
    items.forEach(item => {
        const title = item.querySelector('.nav-title').textContent.toLowerCase();
        const matches = title.includes(lowerQuery);
        item.style.display = matches || query === '' ? 'block' : 'none';
    });
}

// Breadcrumbs generieren
function generateBreadcrumbs() {
    const path = window.location.pathname;
    const parts = path.split('/').filter(p => p && p !== 'econtract');
    
    const breadcrumbs = document.createElement('div');
    breadcrumbs.className = 'breadcrumbs';
    breadcrumbs.innerHTML = `
        <a href="dashboard.html" class="breadcrumb-item">
            <span class="breadcrumb-icon">🏠</span>
            Dashboard
        </a>
        ${parts.map((part, index) => {
            const isLast = index === parts.length - 1;
            const title = part.replace('.html', '').replace(/-/g, ' ');
            return `
                <span class="breadcrumb-separator">›</span>
                ${isLast ? 
                    `<span class="breadcrumb-item active">${title}</span>` :
                    `<a href="${part}" class="breadcrumb-item">${title}</a>`
                }
            `;
        }).join('')}
    `;
    
    return breadcrumbs;
}

// Quick Actions generieren
function generateQuickActions() {
    const actions = [
        { icon: '➕', title: 'Neuer Vertrag', url: 'contract-edit.html?mode=new' },
        { icon: '📥', title: 'Import', url: 'contract-import.html' },
        { icon: '🤖', title: 'KI-Assistent', url: 'ai-assistant.html' },
        { icon: '🔍', title: 'Suche', action: 'openSearch()' }
    ];
    
    const quickActions = document.createElement('div');
    quickActions.className = 'quick-actions';
    quickActions.innerHTML = actions.map(action => `
        <button class="quick-action-btn" 
                ${action.url ? `onclick="window.location='${action.url}'"` : `onclick="${action.action}"`}
                title="${action.title}">
            <span class="quick-action-icon">${action.icon}</span>
            <span class="quick-action-label">${action.title}</span>
        </button>
    `).join('');
    
    return quickActions;
}

// Initialisierung
document.addEventListener('DOMContentLoaded', function() {
    // Prüfen ob unified-menu bereits aktiv ist
    if (document.getElementById('unified-sidebar')) {
        console.log('Unified menu detected - skipping old navigation');
        return; // Unified menu ist aktiv, altes Menü nicht laden
    }
    
    // Navigation einfügen
    const body = document.body;
    const nav = generateNavigation();
    body.insertBefore(nav, body.firstChild);
    
    // Breadcrumbs einfügen
    const container = document.querySelector('.container');
    if (container) {
        const breadcrumbs = generateBreadcrumbs();
        container.insertBefore(breadcrumbs, container.firstChild);
    }
    
    // Quick Actions einfügen
    const header = document.querySelector('header, .page-header');
    if (header) {
        const quickActions = generateQuickActions();
        header.appendChild(quickActions);
    }
    
    // Submenu-Handler
    document.querySelectorAll('.nav-item.has-submenu > .nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            if (window.innerWidth <= 768) {
                e.preventDefault();
                const item = this.closest('.nav-item');
                item.classList.toggle('submenu-open');
            }
        });
    });
    
    // Gespeicherten Zustand wiederherstellen
    if (localStorage.getItem('navCollapsed') === 'true') {
        document.querySelector('.main-navigation').classList.add('collapsed');
    }
    
    // Keyboard Shortcuts
    document.addEventListener('keydown', function(e) {
        // Strg+K: Suche öffnen
        if (e.ctrlKey && e.key === 'k') {
            e.preventDefault();
            document.querySelector('.nav-search-input').focus();
        }
        // Strg+N: Neuer Vertrag
        if (e.ctrlKey && e.key === 'n') {
            e.preventDefault();
            window.location = 'contract-edit.html?mode=new';
        }
        // Strg+I: Import
        if (e.ctrlKey && e.key === 'i') {
            e.preventDefault();
            window.location = 'contract-import.html';
        }
    });
});

// Export für externe Nutzung
window.eContractNavigation = {
    generateNavigation,
    toggleNavigation,
    searchMenu,
    menuStructure
};

