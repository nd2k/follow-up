<script lang="ts">
    let { lastFeedstartTime }: { lastFeedstartTime: string | null } = $props();

    let elapsedMs = $state(0);

    function calculateEllapseTime(startTime: number) {
        return Math.max(0, Date.now() - startTime);
    }

    $effect(() => {
        if (!lastFeedstartTime) {
            elapsedMs = 0;
            return;
        }
        const endTime = new Date(lastFeedstartTime).getTime();
        elapsedMs = calculateEllapseTime(endTime);
        const interval = setInterval(() => {
            elapsedMs = calculateEllapseTime(endTime);
        }, 1000);
        return () => clearInterval(interval);
    })

    function formatEllapseTime(ellapseInMs: number): string {
        const totalSeconds = Math.floor(ellapseInMs / 1000);
        const hour = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        if (hour > 0) return `${hour}h ${String(minutes).padStart(2, "0")}min`;
        return `${minutes}min`;
    }
</script>

<div class="time-since">
  {#if lastFeedstartTime}
    <div class="value">{formatEllapseTime(elapsedMs)}</div>
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
    font-size:  1rem;
    color: var(--muted);
  }
</style>