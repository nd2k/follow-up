<script lang="ts">
  let { lastFeedEndTime }: { lastFeedEndTime: string | null } = $props();

  let elapsedMs = $state(0);

  $effect(() => {
    if (!lastFeedEndTime) {        
      elapsedMs = 0;
      return;
    }
    const end = new Date(lastFeedEndTime).getTime();
    elapsedMs = Math.max(0, Date.now() - end);
    const interval = setInterval(() => {
        elapsedMs = Math.max(0, Date.now() - end);
    }, 1000);
    return () => clearInterval(interval);
  });

  function fmtElapsed(ms: number): string {
    const totalSec = Math.floor(ms / 1000);
    const h = Math.floor(totalSec / 3600);
    const m = Math.floor((totalSec % 3600) / 60);
    if (h > 0) return `${h}h ${String(m).padStart(2, "0")}min`;
    return `${m} min`;
  }
</script>

<div class="time-since">
  {#if lastFeedEndTime}
    <div class="value">{fmtElapsed(elapsedMs)}</div>
    <div class="label">depuis la dernière tétée</div>
  {:else}
    <div class="label">Aucune tétée enregistrée pour l'instant</div>
  {/if}
</div>

<style>
  .time-since {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
  }
  .value {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 30px;
    font-variant-numeric: tabular-nums;
    color: var(--text);
  }
  .label {
    font-size: 12.5px;
    color: var(--muted);
  }
</style>