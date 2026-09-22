<script lang="ts">
  import type { FeedResponse } from "#lib/types/feed.ts";

  let {
    feeds,
    onDelete,
  }: {
    feeds: FeedResponse[];
    onDelete: (id: number) => void;
  } = $props();

  function dayKey(iso: string): string {
    const d = new Date(iso);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  }

  function dayHeading(key: string): string {
    const todayKey = dayKey(new Date().toISOString());
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayKey = dayKey(yesterday.toISOString());
    if (key === todayKey) return "Aujourd'hui";
    if (key === yesterdayKey) return "Hier";
    const [y, m, d] = key.split("-");
    return `${d}/${m}/${y}`;
  }

  function fmtTime(iso: string): string {
    return new Date(iso).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
  }

  interface Session {
    sessionId: number;
    startTime: string;
    feeds: FeedResponse[];
  }
  interface DayGroup {
    key: string;
    sessions: Session[];
    totalMinutes: number;
    count: number;
  }

  let groups = $derived.by((): DayGroup[] => {
    const byDay = new Map<string, FeedResponse[]>();
    for (const feed of feeds) {
      const key = dayKey(feed.startTime);
      if (!byDay.has(key)) byDay.set(key, []);
      byDay.get(key)!.push(feed);
    }

    return Array.from(byDay.entries()).map(([key, dayFeeds]) => {
      const bySession = new Map<number, FeedResponse[]>();
      for (const feed of dayFeeds) {
        if (!bySession.has(feed.sessionId)) bySession.set(feed.sessionId, []);
        bySession.get(feed.sessionId)!.push(feed);
      }

      const sessions: Session[] = Array.from(bySession.entries())
        .map(([sessionId, sessionFeeds]) => ({
          sessionId,
          startTime: sessionFeeds.reduce(
            (earliest, f) => (f.startTime < earliest ? f.startTime : earliest),
            sessionFeeds[0].startTime
          ),
          feeds: sessionFeeds.sort((a, b) => a.startTime.localeCompare(b.startTime)),
        }))
        .sort((a, b) => b.startTime.localeCompare(a.startTime));

      return {
        key,
        sessions,
        totalMinutes: dayFeeds.reduce((sum, f) => sum + (f.durationMinutes ?? 0), 0),
        count: dayFeeds.length,
      };
    });
  });
</script>

<div class="history">
  {#if groups.length === 0}
    <div class="empty">Aucune tétée enregistrée pour l'instant.</div>
  {:else}
    {#each groups as group (group.key)}
      <div class="day-group">
        <div class="day-heading">
          <span>{dayHeading(group.key)}</span>
          <span>{group.count} tétées · {group.totalMinutes} min</span>
        </div>
        {#each group.sessions as session (session.sessionId)}
          <div class="feed-entry">
            <span class="feed-time">{fmtTime(session.startTime)}</span>
            <div class="session-sides">
              {#each session.feeds as feed (feed.id)}
                <span class="side-chip" class:left={feed.breastSide === "LEFT"} class:right={feed.breastSide === "RIGHT"}>
                  <span class="side-dot"></span>
                  {feed.breastSide === "LEFT" ? "G" : "D"} · {feed.durationMinutes} min
                  <button class="feed-delete" aria-label="Supprimer" onclick={() => onDelete(feed.id)}>✕</button>
                </span>
              {/each}
            </div>
          </div>
        {/each}
      </div>
    {/each}
  {/if}
</div>

<style>
  .history { width: 100%; }
  .day-group { margin-bottom: 20px; }
  .day-heading {
    font-size: 12.5px;
    color: var(--muted);
    margin-bottom: 6px;
    display: flex;
    justify-content: space-between;
  }
  .feed-entry {
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--bg);
    border: 1px solid var(--surface-border);
    border-radius: 12px;
    padding: 10px 14px;
    margin-bottom: 6px;
    font-size: 14px;
    flex-wrap: wrap;
  }
  .feed-time { font-weight: 500; min-width: 44px; }
  .session-sides {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    margin-left: auto;
  }
  .side-chip {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 4px 8px;
    border-radius: 999px;
    font-size: 12.5px;
    font-weight: 500;
  }
  .side-chip.left { background: var(--left-soft); color: var(--left); }
  .side-chip.right { background: var(--right-soft); color: var(--right); }
  .side-dot { width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
  .feed-delete {
    background: none;
    border: none;
    color: currentColor;
    opacity: 0.6;
    font-size: 13px;
    cursor: pointer;
    padding: 0 0 0 2px;
    line-height: 1;
  }
  .empty {
    text-align: center;
    color: var(--muted);
    font-size: 14px;
    padding: 20px 0;
  }
</style>