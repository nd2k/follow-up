<script lang="ts">
  import type { BreastSide } from "#lib/types/feed.ts";

  let {
    ongoing,
    breastSide,
    startTime,
    loading,
    onStart,
    onStop,
  }: {
    ongoing: boolean;
    breastSide: BreastSide | null;
    startTime: string | null;
    loading: boolean;
    onStart: () => void;
    onStop: () => void;
  } = $props();

  let elapsedMs = $state(0);

  $effect(() => {
    if (!ongoing || !startTime) {
      elapsedMs = 0;
      return;
    }

    const start = new Date(startTime).getTime();
    elapsedMs = Date.now() - start;

    const interval = setInterval(() => {
      elapsedMs = Date.now() - start;
    }, 1000);

    return () => clearInterval(interval);
  });

  function fmtDuration(ms: number): string {
    const totalSec = Math.floor(ms / 1000);
    const h = Math.floor(totalSec / 3600);
    const m = Math.floor((totalSec % 3600) / 60);
    const s = totalSec % 60;
    if (h > 0) {
      return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
    }
    return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
  }

  let label = $derived(
    ongoing
      ? breastSide === "LEFT" ? "Côté gauche" : "Côté droit"
      : breastSide
        ? `Prêt — côté ${breastSide === "LEFT" ? "gauche" : "droit"}`
        : "Choisis un côté"
  );

  let canStart = $derived(!ongoing && !!breastSide && !loading);
</script>

<div class="timer-wrap">
  <div
    class="timer-ring"
    class:running={ongoing}
    class:left={ongoing && breastSide === "LEFT"}
    class:right={ongoing && breastSide === "RIGHT"}
  >
    <div class="timer-digits">{ongoing ? fmtDuration(elapsedMs) : "00:00"}</div>
    <div class="timer-label">{label}</div>
  </div>
</div>

<button
  class="main-btn"
  class:stop={ongoing}
  disabled={ongoing ? loading : !canStart}
  onclick={ongoing ? onStop : onStart}
>
  {ongoing ? "Arrêter" : "Démarrer"}
</button>

<style>
  .timer-wrap {
    position: relative;
    width: 250px;
    height: 250px;
    margin-bottom: 30px;
  }
  .timer-ring {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 2px solid var(--surface-border);
    background: var(--surface);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8px 24px var(--shadow);
    transition: border-color 0.3s ease;
  }
  .timer-ring.running.left { border-color: var(--left); }
  .timer-ring.running.right { border-color: var(--right); }

  .timer-digits {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 44px;
    font-variant-numeric: tabular-nums;
    letter-spacing: 0.01em;
  }
  .timer-label {
    margin-top: 6px;
    font-size: 13px;
    color: var(--muted);
    min-height: 16px;
  }

  .main-btn {
    width: 100%;
    max-width: 380px;
    padding: 17px 0;
    border-radius: 16px;
    border: none;
    font-family: "Inter", sans-serif;
    font-weight: 600;
    font-size: 16px;
    cursor: pointer;
    color: #fff;
    background: var(--accent);
    margin-bottom: 40px;
    transition: transform 0.08s ease, opacity 0.2s ease;
  }
  .main-btn:active { transform: scale(0.98); }
  .main-btn.stop { background: var(--danger); }
  .main-btn:disabled { opacity: 0.55; cursor: not-allowed; }
</style>