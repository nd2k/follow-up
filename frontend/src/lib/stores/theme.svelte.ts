type ThemeChoice = "light" | "dark" | "system";

const STORAGE_KEY = "allaitement_theme";

function getStoredTheme(): ThemeChoice {
  const stored = localStorage.getItem(STORAGE_KEY);
  return stored === "light" || stored === "dark" ? stored : "system";
}

let choice = $state<ThemeChoice>("system");

function apply(value: ThemeChoice) {
  if (value === "system") {
    document.documentElement.removeAttribute("data-theme");
  } else {
    document.documentElement.setAttribute("data-theme", value);
  }
}

export const theme = {
  get choice() { return choice; },

  init() {
    choice = getStoredTheme();
    apply(choice);
  },

  set(value: ThemeChoice) {
    choice = value;
    apply(value);
    if (value === "system") {
      localStorage.removeItem(STORAGE_KEY);
    } else {
      localStorage.setItem(STORAGE_KEY, value);
    }
  },

  toggle() {
    const next: ThemeChoice = choice === "system" ? "light" : choice === "light" ? "dark" : "system";
    this.set(next);
  },
};