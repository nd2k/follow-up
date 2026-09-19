<script lang="ts">
  import { goto } from "$app/navigation";
  import { auth } from "#lib/stores/auth.svelte.ts";

  let email = $state("");
  let password = $state("");
  let error = $state<string | null>(null);
  let loading = $state(false);

  async function handleSubmit(e: Event) {
    e.preventDefault();
    error = null;
    loading = true;
    try {
      await auth.login(email, password);
      goto("/feeds");
    } catch (err) {
      error = err instanceof Error ? err.message : "Erreur inconnue";
    } finally {
      loading = false;
    }
  }
</script>

<main>
  <h1>Connexion</h1>
  <form onsubmit={handleSubmit}>
    <input type="email" placeholder="Email" bind:value={email} required autocomplete="email" />
    <input type="password" placeholder="Mot de passe" bind:value={password} required autocomplete="current-password" />
    {#if error}
      <p class="error">{error}</p>
    {/if}
    <button type="submit" disabled={loading}>{loading ? "Connexion…" : "Se connecter"}</button>
  </form>
</main>

<style>
  main {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 24px;
  }
  h1 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    margin-bottom: 24px;
  }
  form {
    display: flex;
    flex-direction: column;
    gap: 12px;
    width: 100%;
    max-width: 320px;
  }
  input {
    padding: 13px 16px;
    border-radius: 12px;
    border: 1.5px solid var(--surface-border);
    background: var(--surface);
    color: var(--text);
    font-size: 15px;
  }
  button {
    padding: 14px 0;
    border-radius: 16px;
    border: none;
    background: var(--accent);
    color: #fff;
    font-weight: 600;
    cursor: pointer;
  }
  button:disabled { opacity: 0.6; cursor: not-allowed; }
  .error { color: var(--danger); font-size: 14px; }
</style>