<script lang="ts">
  import type { FeedEntryResponse } from "#lib/types/feed.ts";
  import { editFeedEntry } from "#lib/services/feedService.ts";

  let {
    babyId,
    feedId,
    entry,
    onClose,
    onSaved,
  }: {
    babyId: number;
    feedId: number;
    entry: FeedEntryResponse;
    onClose: () => void;
    onSaved: () => void;
  } = $props();

  function toDatetimeLocal(iso: string): string {
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  const currentEnd = new Date(new Date(entry.startTime).getTime() + entry.durationSeconds * 1000);

  let startLocal = $state(toDatetimeLocal(entry.startTime));
  let endLocal = $state(toDatetimeLocal(currentEnd.toISOString()));
  let saving = $state(false);
  let error = $state<string | null>(null);

  async function handleSubmit(e: Event) {
    e.preventDefault();
    error = null;
    saving = true;
    try {
      const startTime = new Date(startLocal).toISOString();
      const endTime = new Date(endLocal).toISOString();
      await editFeedEntry(babyId, feedId, entry.id, startTime, endTime);
      onSaved();
    } catch (err) {
      error = err instanceof Error ? err.message : "Erreur inconnue";
    } finally {
      saving = false;
    }
  }
</script>

<div
  class="overlay"
  onclick={onClose}
  onkeydown={(e) => e.key === "Escape" && onClose()}
  role="button"
  tabindex="0"
  aria-label="Fermer"
>
  <div class="modal" onclick={(e) => e.stopPropagation()}
    onkeydown={(e) => e.key === "Escape" && onClose()}
    role="button"
    tabindex="0"
    aria-label="Fermer">
  
    <h2>Corriger — {entry.breastSide === "LEFT" ? "Gauche" : "Droite"}</h2>

    <form onsubmit={handleSubmit}>
      <label>
        Début
        <input type="datetime-local" bind:value={startLocal} required />
      </label>
      <label>
        Fin
        <input type="datetime-local" bind:value={endLocal} required />
      </label>

      {#if error}
        <p class="error">{error}</p>
      {/if}

      <div class="actions">
        <button type="button" class="cancel" onclick={onClose}>Annuler</button>
        <button type="submit" class="submit" disabled={saving}>{saving ? "…" : "Enregistrer"}</button>
      </div>
    </form>
  </div>
</div>

<style>
  /* identique à ManualFeedModal.svelte — overlay, modal, form, label, input, actions, error */
  .overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: flex-end;
    justify-content: center;
    z-index: 50;
  }
  .modal {
    width: 100%;
    max-width: 420px;
    background: var(--surface);
    border-radius: 20px 20px 0 0;
    padding: 24px 20px calc(24px + env(safe-area-inset-bottom, 0px));
  }
  h2 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 17px;
    margin: 0 0 16px;
    color: var(--text);
  }
  form { display: flex; flex-direction: column; gap: 12px; }
  label { display: flex; flex-direction: column; gap: 4px; font-size: 13px; color: var(--muted); }
  input {
    padding: 12px 14px;
    border-radius: 12px;
    border: 1.5px solid var(--surface-border);
    background: var(--bg);
    color: var(--text);
    font-size: 15px;
  }
  .actions { display: flex; gap: 10px; margin-top: 8px; }
  .actions button { flex: 1; padding: 13px 0; border-radius: 14px; border: none; font-weight: 600; cursor: pointer; }
  .cancel { background: var(--bg); color: var(--text); border: 1.5px solid var(--surface-border) !important; }
  .submit { background: var(--accent); color: #fff; }
  .submit:disabled { opacity: 0.6; cursor: not-allowed; }
  .error { color: var(--danger); font-size: 13px; margin: 0; }
</style>