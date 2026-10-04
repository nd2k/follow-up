<script lang="ts">
  import { recordManualFeed } from "#lib/services/feedService.ts";

  let { babyId, onClose, onSaved }: { babyId: number; onClose: () => void; onSaved: () => void } = $props();

  function toDatetimeLocal(d: Date): string {
    const pad = (n: number) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  const now = new Date();
  const fifteenMinAgo = new Date(now.getTime() - 15 * 60000);
  const defaultStart = toDatetimeLocal(fifteenMinAgo);
  const defaultEnd = toDatetimeLocal(now);

  let leftEnabled = $state(true);
  let leftStart = $state(defaultStart);
  let leftEnd = $state(defaultEnd);

  let rightEnabled = $state(false);
  let rightStart = $state(defaultStart);
  let rightEnd = $state(defaultEnd);

  let saving = $state(false);
  let error = $state<string | null>(null);

  async function handleSubmit(e: Event) {
    e.preventDefault();
    error = null;

    if (!leftEnabled && !rightEnabled) {
      error = "Sélectionne au moins un côté";
      return;
    }

    saving = true;
    try {
      const entries = [];
      if (leftEnabled) {
        entries.push({ breastSide: "LEFT" as const, startTime: new Date(leftStart).toISOString(), endTime: new Date(leftEnd).toISOString() });
      }
      if (rightEnabled) {
        entries.push({ breastSide: "RIGHT" as const, startTime: new Date(rightStart).toISOString(), endTime: new Date(rightEnd).toISOString() });
      }
      await recordManualFeed(babyId, entries);
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
  <div class="modal" 
    onclick={(e) => e.stopPropagation()} 
    onkeydown={(e) => e.key === "Escape" && onClose()}
    role="button"
    tabindex="0"
    aria-label="Fermer">
    <h2>Ajouter une tétée</h2>

    <form onsubmit={handleSubmit}>
      <div class="side-section" class:enabled={leftEnabled}>
        <label class="side-check">
          <input type="checkbox" bind:checked={leftEnabled} />
          Gauche
        </label>
        {#if leftEnabled}
          <div class="time-row">
            <label>Début <input type="datetime-local" bind:value={leftStart} required /></label>
            <label>Fin <input type="datetime-local" bind:value={leftEnd} required /></label>
          </div>
        {/if}
      </div>

      <div class="side-section" class:enabled={rightEnabled}>
        <label class="side-check">
          <input type="checkbox" bind:checked={rightEnabled} />
          Droite
        </label>
        {#if rightEnabled}
          <div class="time-row">
            <label>Début <input type="datetime-local" bind:value={rightStart} required /></label>
            <label>Fin <input type="datetime-local" bind:value={rightEnd} required /></label>
          </div>
        {/if}
      </div>

      {#if error}
        <p class="error">{error}</p>
      {/if}

      <div class="actions">
        <button type="button" class="cancel" onclick={onClose}>Annuler</button>
        <button type="submit" class="submit" disabled={saving}>{saving ? "…" : "Ajouter"}</button>
      </div>
    </form>
  </div>
</div>

<style>
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
    max-height: 85vh;
    overflow-y: auto;
  }
  h2 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 17px;
    margin: 0 0 16px;
    color: var(--text);
  }
  form {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }
  .side-section {
    border: 1.5px solid var(--surface-border);
    border-radius: 14px;
    padding: 12px 14px;
  }
  .side-section.enabled {
    border-color: var(--accent);
  }
  .side-check {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    font-size: 14.5px;
    color: var(--text);
    cursor: pointer;
  }
  .side-check input[type="checkbox"] {
    width: 18px;
    height: 18px;
  }
  .time-row {
    display: flex;
    gap: 10px;
    margin-top: 10px;
  }
  .time-row label {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 12px;
    color: var(--muted);
  }
  input[type="datetime-local"] {
    padding: 10px 12px;
    border-radius: 10px;
    border: 1.5px solid var(--surface-border);
    background: var(--bg);
    color: var(--text);
    font-size: 13.5px;
  }
  .actions {
    display: flex;
    gap: 10px;
    margin-top: 4px;
  }
  .actions button {
    flex: 1;
    padding: 13px 0;
    border-radius: 14px;
    border: none;
    font-weight: 600;
    cursor: pointer;
  }
  .cancel {
    background: var(--bg);
    color: var(--text);
    border: 1.5px solid var(--surface-border) !important;
  }
  .submit {
    background: var(--accent);
    color: #fff;
  }
  .submit:disabled { opacity: 0.6; cursor: not-allowed; }
  .error { color: var(--danger); font-size: 13px; margin: 0; }
</style>