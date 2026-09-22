<script lang="ts">
  import type { FeedResponse } from "#lib/types/feed.ts";

  let { feeds, dayKey }: { feeds: FeedResponse[]; dayKey: string } = $props();

  const HOUR_MARKS = [0, 6, 12, 18, 24];

  function percentOfDay(iso: string): number {
    const d = new Date(iso);
    const minutesSinceMidnight = d.getHours() * 60 + d.getMinutes();
    return (minutesSinceMidnight / 1440) * 100;
  }

  interface Segment {
    id: number;
    side: "LEFT" | "RIGHT";
    leftPercent: number;
    widthPercent: number;
    timeLabel: string;
  }

  let segments = $derived.by((): Segment[] => {
    return feeds
      .filter((f) => !f.ongoing)
      .map((f) => {
        const start = percentOfDay(f.startTime);
        const end = f.endTime ? percentOfDay(f.endTime) : start;
        const width = Math.max(end - start, 0.6); // largeur minimale pour rester visible
        return {
          id: f.id,
          side: f.side,
          leftPercent: start,
          widthPercent: width,
          timeLabel: new Date(f.startTime).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" }),
        };
      });
  });

  let formattedDay = $derived(
    new Date(dayKey).toLocaleDateString("fr-FR", { weekday: "long", day: "numeric", month: "long" })
  );
</script>

<div class="timeline-wrap">
  <p class="day-title">{formattedDay}</p>

  {#if segments.length === 0}
    <p class="empty">Aucune tétée ce jour-là.</p>
  {:else}
    <div class="timeline">
      {#each segments as seg (seg.id)}
        <div
          class="segment"
          class:left={seg.side === "LEFT"}
          class:right={seg.side === "RIGHT"}
          style:left="{seg.leftPercent}%"
          style:width="{seg.widthPercent}%"
          title="{seg.timeLabel}"
        ></div>
      {/each}
    </div>
    <div class="hour-marks">
      {#each HOUR_MARKS as h}
        <span>{h}h</span>
      {/each}
    </div>
  {/if}

  <div class="legend">
    <span class="legend-item"><span class="dot left"></span> Gauche</span>
    <span class="legend-item"><span class="dot right"></span> Droite</span>
  </div>
</div>

<style>
  .timeline-wrap { width: 100%; }
  .day-title {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 15px;
    margin: 0 0 12px;
    text-transform: capitalize;
  }
  .timeline {
    position: relative;
    height: 36px;
    background: var(--bg);
    border: 1px solid var(--surface-border);
    border-radius: 10px;
    overflow: hidden;
  }
  .segment {
    position: absolute;
    top: 4px;
    bottom: 4px;
    border-radius: 4px;
  }
  .segment.left { background: var(--left); }
  .segment.right { background: var(--right); }
  .hour-marks {
    display: flex;
    justify-content: space-between;
    margin-top: 4px;
    font-size: 11px;
    color: var(--muted);
  }
  .legend {
    display: flex;
    gap: 16px;
    margin-top: 14px;
  }
  .legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: var(--muted);
  }
  .dot { width: 8px; height: 8px; border-radius: 50%; }
  .dot.left { background: var(--left); }
  .dot.right { background: var(--right); }
  .empty { color: var(--muted); font-size: 14px; }
</style>