<script lang="ts">
  import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";
  import { editFeedEntry, addEntryToFeed, deleteFeedEntry } from "#lib/services/feedService.ts";
  let {
    babyId,
    feed,
    onClose,
    onSaved,
  }: {
    babyId: number;
    feed: FeedResponse;
    onClose: () => void;
    onSaved: () => void;
  } = $props();

  function toDatetimeLocal(iso: string): string {
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  function entryEnd(entry: (typeof feed.entries)[number]): Date {
    return new Date(new Date(entry.startTime).getTime() + entry.durationSeconds * 1000);
  }

  let leftEntry = $derived(feed.entries.find((e) => e.breastSide === "LEFT") ?? null);
  let rightEntry = $derived(feed.entries.find((e) => e.breastSide === "RIGHT") ?? null);

  const initialLeftStart = $derived(leftEntry ? toDatetimeLocal(leftEntry.startTime) : "");
  const initialLeftEnd = $derived(leftEntry ? toDatetimeLocal(entryEnd(leftEntry).toISOString()) : "");
  const initialRightStart = $derived(rightEntry ? toDatetimeLocal(rightEntry.startTime) : "");
  const initialRightEnd = $derived(rightEntry ? toDatetimeLocal(entryEnd(rightEntry).toISOString()) : "");


  let leftStart = $derived(initialLeftStart);
  let leftEnd = $derived(initialLeftEnd);
  let rightStart = $derived(initialRightStart);
  let rightEnd = $derived(initialRightEnd);

  let addingLeft = $state(false);
  let addingRight = $state(false);
  let saving = $state(false);
  let error = $state<string | null>(null);

    

  function startAdding(breastSide: BreastSide) {
    const now = toDatetimeLocal(new Date().toISOString());
    if (breastSide === "LEFT") { addingLeft = true; leftStart = now; leftEnd = now; }
    else { 
        addingRight = true; rightStart = now; rightEnd = now;         
    }
  }

  async function handleSave() {
    error = null;
    saving = true;
    const leftChanged = leftStart !== initialLeftStart || leftEnd !== initialLeftEnd;
    const rightChanged = rightStart !== initialRightStart || rightEnd !== initialRightEnd;
    const results = await Promise.allSettled([
    leftEntry && leftChanged
        ? editFeedEntry(babyId, feed.id, leftEntry.id, new Date(leftStart).toISOString(), new Date(leftEnd).toISOString())
        : !leftEntry && addingLeft
          ? addEntryToFeed(babyId, feed.id, "LEFT", new Date(leftStart).toISOString(), new Date(leftEnd).toISOString())
          : Promise.resolve(),
      rightEntry && rightChanged
        ? editFeedEntry(babyId, feed.id, rightEntry.id, new Date(rightStart).toISOString(), new Date(rightEnd).toISOString())
        : !rightEntry && addingRight
          ? addEntryToFeed(babyId, feed.id, "RIGHT", new Date(rightStart).toISOString(), new Date(rightEnd).toISOString())
          : Promise.resolve(),
    ]);
    const failures = results.filter((r) => r.status === "rejected");
    saving = false;
    if (failures.length > 0) {
      error = failures.map((f) => (f as PromiseRejectedResult).reason?.message ?? "Erreur inconnue").join(" · ");
      return;
    }
    onSaved();
  }

  async function handleDelete(entryId: number) {
    error = null;
    try {
      await deleteFeedEntry(babyId, feed.id, entryId);
      onSaved();
    } catch (err) {
      error = err instanceof Error ? err.message : "Erreur inconnue";
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
  <div class="modal" 
    onclick={(e) => e.stopPropagation()}
    onkeydown={(e) => e.key === "Escape" && onClose()}
    role="button"
    tabindex="0"
    aria-label="Fermer">

    <h2>Détails de la tétée</h2>

    <div class="side-block" class:filled={!!leftEntry}>
      <div class="side-head">
        <span class="side-name left">Gauche</span>
        {#if leftEntry}
          <button class="delete-link" onclick={() => handleDelete(leftEntry.id)}>Supprimer</button>
        {/if}
      </div>
      {#if leftEntry || addingLeft}
        <div class="time-row">
          <label>Début <input type="datetime-local" bind:value={leftStart} /></label>
          <label>Fin <input type="datetime-local" bind:value={leftEnd} /></label>
        </div>
      {:else}
        <button class="add-side-btn" onclick={() => startAdding("LEFT")}>+ Ajouter le côté gauche (oublié)</button>
      {/if}
    </div>

    <div class="side-block" class:filled={!!rightEntry}>
      <div class="side-head">
        <span class="side-name right">Droite</span>
        {#if rightEntry}
          <button class="delete-link" onclick={() => handleDelete(rightEntry.id)}>Supprimer</button>
        {/if}
      </div>
      {#if rightEntry || addingRight}
        <div class="time-row">
          <label>Début <input type="datetime-local" bind:value={rightStart} /></label>
          <label>Fin <input type="datetime-local" bind:value={rightEnd} /></label>
        </div>
      {:else}
        <button class="add-side-btn" onclick={() => startAdding("RIGHT")}>+ Ajouter le côté droit (oublié)</button>
      {/if}
    </div>

    {#if error}
      <p class="error">{error}</p>
    {/if}

    <div class="actions">
      <button class="cancel" onclick={onClose}>Fermer</button>
      <button class="submit" disabled={saving} onclick={handleSave}>{saving ? "…" : "Enregistrer"}</button>
    </div>
  </div>
</div>

<style>
  .overlay {
    position: fixed; inset: 0; background: rgba(0, 0, 0, 0.4);
    display: flex; align-items: flex-end; justify-content: center; z-index: 50;
  }
  .modal {
    width: 100%; max-width: 420px; background: var(--surface);
    border-radius: 20px 20px 0 0; padding: 24px 20px calc(24px + env(safe-area-inset-bottom, 0px));
    max-height: 85vh; overflow-y: auto;
  }
  h2 {
    font-family: "Fraunces", serif; font-weight: 500; font-size: 17px;
    margin: 0 0 16px; color: var(--text);
  }
  .side-block {
    border: 1.5px solid var(--surface-border);
    border-radius: 14px;
    padding: 12px 14px;
    margin-bottom: 12px;
  }
  .side-block.filled { border-color: var(--surface-border); }
  .side-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 4px; }
  .side-name { font-weight: 600; font-size: 14.5px; }
  .side-name.left { color: var(--left); }
  .side-name.right { color: var(--right); }
  .delete-link { background: none; border: none; color: var(--danger); font-size: 12.5px; cursor: pointer; }
  .time-row { display: flex; gap: 10px; margin-top: 8px; }
  .time-row label { flex: 1; display: flex; flex-direction: column; gap: 4px; font-size: 12px; color: var(--muted); }
  input[type="datetime-local"] {
    padding: 10px 12px; border-radius: 10px; border: 1.5px solid var(--surface-border);
    background: var(--bg); color: var(--text); font-size: 13.5px;
  }
  .add-side-btn {
    width: 100%; padding: 10px 0; border-radius: 10px;
    border: 1.5px dashed var(--surface-border); background: none;
    color: var(--muted); font-size: 13px; font-weight: 500; cursor: pointer;
  }
  .actions { display: flex; gap: 10px; margin-top: 4px; }
  .actions button { flex: 1; padding: 13px 0; border-radius: 14px; border: none; font-weight: 600; cursor: pointer; }
  .cancel { background: var(--bg); color: var(--text); border: 1.5px solid var(--surface-border) !important; }
  .submit { background: var(--accent); color: #fff; }
  .submit:disabled { opacity: 0.6; cursor: not-allowed; }
  .error { color: var(--danger); font-size: 13px; margin: 8px 0 0; }
</style>