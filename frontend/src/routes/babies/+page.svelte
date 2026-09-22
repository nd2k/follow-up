<script lang="ts">
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { babyStore } from "#lib/stores/selectedBaby.svelte.ts";
  import { createBaby } from "#lib/services/babyService.ts";

  let name = $state("");
  let birthDate = $state("");
  let creating = $state(false);
  let error = $state<string | null>(null);

  onMount(() => {
    babyStore.refresh();
  });

  async function handleCreate(e: Event) {
    e.preventDefault();
    error = null;
    creating = true;
    try {
      const baby = await createBaby(name, birthDate || null);
      await babyStore.refresh();
      babyStore.select(baby.id);
      name = "";
      birthDate = "";
      goto("/feeds");
    } catch (err) {
      error = err instanceof Error ? err.message : "Erreur inconnue";
    } finally {
      creating = false;
    }
  }
</script>

<main>
  <h1>Bébés</h1>

  {#if babyStore.babies.length > 0}
    <div class="baby-list">
      {#each babyStore.babies as baby (baby.id)}
        <button 
          class="baby-card" 
          class:active={babyStore.selectedId === baby.id}
          onclick={() => babyStore.select(baby.id)}>
          <span class="baby-name">{baby.name}</span>
          {#if baby.birthDate}
            <span class="baby-date">Né(e) le {new Date(baby.birthDate).toLocaleDateString("fr-FR")}</span>
          {/if}
        </button>
      {/each}
    </div>
  {/if}

  <h2>Ajouter un bébé</h2>
  <form onsubmit={handleCreate}>
    <input type="text" placeholder="Prénom" bind:value={name} required />
    <input type="date" bind:value={birthDate} />
    {#if error}
      <p class="error">{error}</p>
    {/if}
    <button type="submit" disabled={creating}>{creating ? "Création…" : "Ajouter"}</button>
  </form>

  <a href="/feeds" class="back-link">← Retour au suivi</a>
</main>

<style>
  main {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px 20px 48px;
    max-width: 420px;
    margin: 0 auto;
  }
  h1 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 22px;
    margin: 4px 0 20px;
  }
  h2 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 16px;
    align-self: flex-start;
    margin: 24px 0 12px;
  }
  .baby-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
  }
  .baby-card {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: 14px 16px;
    border-radius: 14px;
    border: 1px solid var(--surface-border);
    background: var(--surface);
    color: var(--text);
    cursor: pointer;
    text-align: left;
    width: 100%;
  }
  .baby-name { font-weight: 600; font-size: 15px; }
  .baby-date { font-size: 12.5px; color: var(--muted); }

  form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
  }
  input {
    padding: 13px 16px;
    border-radius: 12px;
    border: 1.5px solid var(--surface-border);
    background: var(--surface);
    color: var(--text);
    font-size: 15px;
  }
  button[type="submit"] {
    padding: 14px 0;
    border-radius: 16px;
    border: none;
    background: var(--accent);
    color: #fff;
    font-weight: 600;
    cursor: pointer;
  }
  button[type="submit"]:disabled { opacity: 0.6; cursor: not-allowed; }
  .error { color: var(--danger); font-size: 14px; }

  .baby-card.active {
    border-color: var(--accent);
  }
  .back-link {
    margin-top: 24px;
    color: var(--muted);
    font-size: 14px;
    text-decoration: none;
  }
</style>