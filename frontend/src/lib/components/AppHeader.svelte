<script lang="ts">
  import { goto } from "$app/navigation";
  import { auth } from "#lib/stores/auth.svelte.ts";
  import { theme } from "#lib/stores/theme.svelte.ts";

  let menuOpen = $state(false);
  let menuRef: HTMLDivElement;

  function toggleMenu() {
    menuOpen = !menuOpen;
  }

  function closeMenu() {
    menuOpen = false;
  }

  function handleClickOutside(e: MouseEvent) {
    if (menuOpen && menuRef && !menuRef.contains(e.target as Node)) {
      closeMenu();
    }
  }

  function goToBabies() {
    closeMenu();
    goto("/babies");
  }

  function handleLogout() {
    closeMenu();
    auth.logout();
    goto("/login");
  }

  $effect(() => {
    if (menuOpen) {
      window.addEventListener("click", handleClickOutside);
      return () => window.removeEventListener("click", handleClickOutside);
    }
  });

  function themeLabel(choice: string): string {
    if (choice === "light") return "Thème : clair";
    if (choice === "dark") return "Thème : sombre";
    return "Thème : auto";
  }
</script>

<header>
  <h1>Follow-up</h1>

  <div class="menu-wrap" bind:this={menuRef}>
    <button class="burger" onclick={toggleMenu} aria-label="Menu">
      <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
        <path d="M3 6h16M3 11h16M3 16h16" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
      </svg>
    </button>

    {#if menuOpen}
      <div class="dropdown">
        <button onclick={goToBabies}>Gérer les bébés</button>
        <button onclick={() => { closeMenu(); goto("/calendar"); }}>Calendrier</button>
        <button onclick={() => theme.toggle()}>{themeLabel(theme.choice)}</button>
        <button class="danger" onclick={handleLogout}>Se déconnecter</button>
      </div>
    {/if}
  </div>
</header>

<style>
  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 20px 12px;
    max-width: 420px;
    margin: 0 auto;
    width: 100%;
    box-sizing: border-box;
  }
  h1 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 28px;
    margin: 0;
    color: var(--text);
  }
  .menu-wrap {
    position: relative;
  }
  .burger {
    background: none;
    border: none;
    color: var(--text);
    cursor: pointer;
    padding: 8px;
    display: flex;
  }
  .dropdown {
    position: absolute;
    top: calc(100% + 6px);
    right: 0;
    background: var(--surface);
    border: 1px solid var(--surface-border);
    border-radius: 14px;
    box-shadow: 0 8px 24px var(--shadow);
    overflow: hidden;
    min-width: 180px;
    z-index: 20;
  }
  .dropdown button {
    display: block;
    width: 100%;
    text-align: left;
    padding: 13px 16px;
    background: none;
    border: none;
    color: var(--text);
    font-size: 14.5px;
    cursor: pointer;
  }
  .dropdown button:not(:last-child) {
    border-bottom: 1px solid var(--surface-border);
  }
  .dropdown button.danger {
    color: var(--danger);
  }
</style>