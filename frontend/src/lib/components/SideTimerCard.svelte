<script lang="ts">
  import type { BreastSide } from "#lib/types/feed.ts";

  let {
    breastSide,
    ongoing,
    startTime,
    isPendingSave,
    frozenEndTime,
    loading,
    syncError,
    onStart,
    onStopClock,
  }: {
    breastSide: BreastSide;
    ongoing: boolean;
    startTime: string | null;
    isPendingSave: boolean;
    frozenEndTime: string | null;
    loading: boolean;
    syncError: boolean;
    onStart: () => void;
    onStopClock: () => void;
  } = $props();

  let elapsedMs = $state(0);

  $effect(() => {
    if (isPendingSave && startTime && frozenEndTime) {
      elapsedMs = new Date(frozenEndTime).getTime() - new Date(startTime).getTime();
      return;
    }
    if (!ongoing || !startTime) {
      elapsedMs = 0;
      return;
    }
    const start = new Date(startTime).getTime();
    elapsedMs = Date.now() - start;
    const interval = setInterval(() => { elapsedMs = Date.now() - start; }, 1000);
    return () => clearInterval(interval);
  });

  function fmtDuration(ms: number): string {
    const totalSec = Math.floor(ms / 1000);
    const h = Math.floor(totalSec / 3600);
    const m = Math.floor((totalSec % 3600) / 60);
    const s = totalSec % 60;
    if (h > 0) return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
    return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
  }

  let label = $derived(breastSide === "LEFT" ? "Gauche" : "Droite");
  let showDigits = $derived(ongoing || isPendingSave);
</script>

<div class="side-timer" class:running={ongoing && !isPendingSave} class:pending={isPendingSave} class:left={breastSide === "LEFT"} class:right={breastSide === "RIGHT"}>
  <div class="ring">
    <div class="digits">{showDigits ? fmtDuration(elapsedMs) : "00:00"}</div>
    <div class="label">{label}{isPendingSave ? " · à enregistrer" : ""}</div>
  </div>

  <button
    class="btn"
    class:stop={ongoing}
    disabled={isPendingSave || (!ongoing && loading)}
    onclick={ongoing ? onStopClock : onStart}
  >
    {ongoing ? "Arrêter" : "Démarrer"}
  </button>

  {#if syncError}
    <div class="sync-warning">Synchronisation…</div>
  {/if}
</div>

<style>
  .side-timer {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    flex: 1;
  }
  .ring {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 2px solid var(--surface-border);
    background: var(--bg);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    transition: border-color 0.3s ease;
  }
  .side-timer.running.left .ring { border-color: var(--left); }
  .side-timer.running.right .ring { border-color: var(--right); }
  .side-timer.pending .ring { border-color: var(--accent); }
  .digits {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 22px;
    font-variant-numeric: tabular-nums;
  }
  .label { font-size: 11px; color: var(--muted); margin-top: 4px; text-align: center; }
  .btn {
    width: 100%;
    padding: 12px 0;
    border-radius: 14px;
    border: none;
    font-family: "Inter", sans-serif;
    font-weight: 600;
    font-size: 14px;
    color: #fff;
    cursor: pointer;
  }
  .side-timer.left .btn { background: var(--left); }
  .side-timer.right .btn { background: var(--right); }
  .btn.stop { background: var(--danger); }
  .btn:disabled { opacity: 0.4; cursor: not-allowed; }
  .sync-warning { font-size: 11px; color: var(--muted); }
</style>