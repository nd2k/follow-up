type ThemeChoice = "light" | "dark";

const STORAGE_KEY = "follow-up-theme";

function getStoredTheme(): ThemeChoice {
    const storedTheme = localStorage.getItem(STORAGE_KEY);
    return storedTheme === "light" ? "light" : "dark";
}

let themeChoice = $state<ThemeChoice>("light");

function applyTheme(value: ThemeChoice) {
    document.documentElement.setAttribute("data-theme", value);
}

export const theme = {
    get themeChoice() { return themeChoice; },

    init() {
        themeChoice = getStoredTheme();
        applyTheme(themeChoice);
    },

    set(value: ThemeChoice) {
        themeChoice = value;
        applyTheme(value);
        localStorage.setItem(STORAGE_KEY, value);
    },

    toggleTheme() {
        const next: ThemeChoice = themeChoice === 'light' ? "dark" : "light";
        this.set(next);
    }
}