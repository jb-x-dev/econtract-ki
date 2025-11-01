/**
 * Zentrale Menü-Konfiguration für eContract KI
 * Diese Datei definiert die komplette Menüstruktur
 */

const MENU_CONFIG = {
    // Logo und Branding
    branding: {
        logo: '📋',
        title: 'eContract KI',
        subtitle: 'Intelligente Vertragsverwaltung'
    },

    // Hauptmenü-Struktur
    mainMenu: [
        {
            id: 'dashboard',
            icon: '📊',
            label: 'Dashboard',
            url: '/econtract/dashboard.html',
            description: 'Übersicht und Statistiken'
        },
        {
            id: 'contracts',
            icon: '📝',
            label: 'Verträge',
            url: '/econtract/contracts.html',
            description: 'Vertragsverwaltung',
            submenu: [
                { label: 'Alle Verträge', url: '/econtract/contracts.html' },
                { label: 'Neuer Vertrag', url: '/econtract/contract-edit.html?mode=new' },
                { label: 'Entwürfe', url: '/econtract/contracts.html#drafts' },
                { label: 'Archiv', url: '/econtract/contracts.html#archive' }
            ]
        },
        {
            id: 'import',
            icon: '📥',
            label: 'Import & OCR',
            url: '/econtract/contract-import.html',
            badge: { text: 'KI', color: 'red' },
            description: 'Intelligenter Import',
            submenu: [
                { label: 'Datei hochladen', url: '/econtract/contract-import.html' },
                { label: 'OCR-Scan', url: '/econtract/ocr-scan.html' },
                { label: 'Arbeitsvorrat', url: '/econtract/contract-import.html#queue' },
                { label: 'Batch-Import', url: '/econtract/contract-import.html#batch' },
                { label: 'Import-Historie', url: '/econtract/contract-import.html#history' }
            ]
        },
        {
            id: 'ai-assistant',
            icon: '🤖',
            label: 'KI-Assistent',
            url: '/econtract/ai-assistant.html',
            badge: { text: 'KI', color: 'red' },
            description: 'KI-gestützte Funktionen',
            submenu: [
                { label: 'Vertrag generieren', url: '/econtract/ai-assistant.html#generate' },
                { label: 'Risiken analysieren', url: '/econtract/ai-assistant.html#analyze' },
                { label: 'Klauseln vorschlagen', url: '/econtract/ai-assistant.html#clauses' },
                { label: 'Vertrag zusammenfassen', url: '/econtract/ai-assistant.html#summary' },
                { label: 'Verträge vergleichen', url: '/econtract/ai-assistant.html#compare' }
            ]
        },
        {
            id: 'framework',
            icon: '📑',
            label: 'Rahmenverträge',
            url: '/econtract/framework-contracts.html',
            description: 'Rahmenverträge verwalten',
            submenu: [
                { label: 'Übersicht', url: '/econtract/framework-contracts.html' },
                { label: 'Neuer Rahmenvertrag', url: '/econtract/framework-contracts.html#new' },
                { label: 'Volumen-Tracking', url: '/econtract/framework-contracts.html#volume' }
            ]
        },
        {
            id: 'workflows',
            icon: '✅',
            label: 'Genehmigungen',
            url: '/econtract/workflows.html',
            description: 'Genehmigungsworkflows',
            submenu: [
                { label: 'Meine Aufgaben', url: '/econtract/workflows.html#my-tasks' },
                { label: 'Alle Workflows', url: '/econtract/workflows.html' },
                { label: 'Workflow-Designer', url: '/econtract/workflows.html#designer' }
            ]
        },
        {
            id: 'deadlines',
            icon: '⏰',
            label: 'Fristen',
            url: '/econtract/deadlines.html',
            description: 'Fristenmanagement',
            submenu: [
                { label: 'Kritische Fristen', url: '/econtract/deadlines.html#critical' },
                { label: 'Alle Fristen', url: '/econtract/deadlines.html' },
                { label: 'Kündigungsfristen', url: '/econtract/deadlines.html#termination' },
                { label: 'Verlängerungen', url: '/econtract/deadlines.html#renewal' }
            ]
        },
        {
            id: 'calendar',
            icon: '📅',
            label: 'Kalender',
            url: '/econtract/calendar.html',
            description: 'Kalenderansicht'
        },
        {
            id: 'maintenance',
            icon: '🔧',
            label: 'Pflege',
            url: '/econtract/maintenance.html',
            description: 'Vertragspflege',
            submenu: [
                { label: 'Wartungsaufgaben', url: '/econtract/maintenance.html' },
                { label: 'Neue Aufgabe', url: '/econtract/maintenance.html#new' },
                { label: 'Überfällige', url: '/econtract/maintenance.html#overdue' }
            ]
        },
        {
            id: 'reports',
            icon: '📊',
            label: 'Berichte',
            url: '/econtract/reports/dashboard.html',
            badge: { text: 'NEU', color: 'green' },
            description: 'Berichte und Auswertungen',
            submenu: [
                { label: 'Dashboard', url: '/econtract/reports/dashboard.html' },
                { label: 'Vertragsübersicht', url: '/econtract/reports/contracts.html' },
                { label: 'Zahlungsverfolgung', url: '/econtract/reports/payments.html' },
                { label: 'Compliance', url: '/econtract/reports/compliance.html' }
            ]
        }
    ],

    // Administration-Menü
    adminMenu: [
        {
            id: 'users',
            icon: '👥',
            label: 'Benutzer',
            url: '/econtract/admin/users.html',
            description: 'Benutzerverwaltung',
            submenu: [
                { label: 'Alle Benutzer', url: '/econtract/admin/users.html' },
                { label: 'Rollen', url: '/econtract/admin/roles.html' },
                { label: 'Gruppen', url: '/econtract/admin/groups.html' }
            ]
        },
        {
            id: 'settings',
            icon: '⚙️',
            label: 'Einstellungen',
            url: '/econtract/settings/general.html',
            description: 'Systemeinstellungen',
            submenu: [
                { label: 'Allgemein', url: '/econtract/settings/general.html' },
                { label: 'E-Mail', url: '/econtract/settings/email.html' },
                { label: 'KI-Konfiguration', url: '/econtract/settings/ai.html' },
                { label: 'System', url: '/econtract/settings/system.html' }
            ]
        },
        {
            id: 'api-docs',
            icon: '📚',
            label: 'API-Dokumentation',
            url: '/econtract/swagger-ui.html',
            description: 'Swagger API Docs',
            external: true
        }
    ],

    // User-Menü
    userMenu: [
        {
            id: 'profile',
            icon: '👤',
            label: 'Mein Profil',
            url: '/econtract/profile.html'
        },
        {
            id: 'notifications',
            icon: '🔔',
            label: 'Benachrichtigungen',
            url: '/econtract/notifications.html',
            badge: { text: '3', color: 'red' }
        },
        {
            id: 'help',
            icon: '❓',
            label: 'Hilfe',
            url: '/econtract/help/docs.html',
            submenu: [
                { label: 'Dokumentation', url: '/econtract/help/docs.html' },
                { label: 'FAQ', url: '/econtract/help/faq.html' },
                { label: 'Support', url: '/econtract/help/support.html' }
            ]
        },
        {
            id: 'logout',
            icon: '🚪',
            label: 'Abmelden',
            url: '#',
            action: 'logout'
        }
    ],

    // Schnellaktionen (Quick Actions)
    quickActions: [
        { icon: '➕', label: 'Neuer Vertrag', url: '/econtract/contract-edit.html?mode=new' },
        { icon: '📥', label: 'Import', url: '/econtract/contract-import.html' },
        { icon: '🤖', label: 'KI-Assistent', url: '/econtract/ai-assistant.html' },
        { icon: '🔍', label: 'Suche', action: 'search' }
    ],

    // Keyboard Shortcuts
    shortcuts: {
        'Ctrl+K': { action: 'search', description: 'Suche öffnen' },
        'Ctrl+N': { action: 'new-contract', description: 'Neuer Vertrag' },
        'Ctrl+I': { action: 'import', description: 'Import öffnen' }
    }
};

// Hilfsfunktion: Aktuellen Menüpunkt finden
function getCurrentMenuItem() {
    const currentPath = window.location.pathname + window.location.search;
    
    // Suche in Hauptmenü
    for (const item of MENU_CONFIG.mainMenu) {
        if (currentPath.includes(item.url)) return item;
        if (item.submenu) {
            for (const sub of item.submenu) {
                if (currentPath.includes(sub.url)) return item;
            }
        }
    }
    
    // Suche in Admin-Menü
    for (const item of MENU_CONFIG.adminMenu) {
        if (currentPath.includes(item.url)) return item;
        if (item.submenu) {
            for (const sub of item.submenu) {
                if (currentPath.includes(sub.url)) return item;
            }
        }
    }
    
    // Suche in User-Menü
    for (const item of MENU_CONFIG.userMenu) {
        if (currentPath.includes(item.url)) return item;
    }
    
    return null;
}

// Hilfsfunktion: Breadcrumbs generieren
function generateBreadcrumbs() {
    const currentPath = window.location.pathname;
    const breadcrumbs = [
        { label: 'Dashboard', url: '/econtract/dashboard.html', icon: '🏠' }
    ];
    
    const currentItem = getCurrentMenuItem();
    if (currentItem && currentItem.id !== 'dashboard') {
        breadcrumbs.push({
            label: currentItem.label,
            url: currentItem.url,
            icon: currentItem.icon
        });
    }
    
    return breadcrumbs;
}

// Export für Verwendung in anderen Dateien
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { MENU_CONFIG, getCurrentMenuItem, generateBreadcrumbs };
}

