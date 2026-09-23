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

  function fmtDelay(ms: number): string {
    const totalMin = Math.round(ms / 60000);
    const h = Math.floor(totalMin / 60);
    const m = totalMin % 60;
    if (h > 0) return `${h}h${String(m).padStart(2, "0")}`;
    return `${m} min`;
  }

  interface SessionRow {
    dayKey: string;
    sessionId: number;
    startTime: string;
    endTime: string; // fin la plus tardive parmi les côtés de la session
    feeds: FeedResponse[];
    delaySincePrevious: number | null; // ms depuis la fin de la session précédente, null pour la toute première
  }

  // Construit la séquence globale des sessions, triée du plus ancien au plus récent,
  // pour que le calcul de délai traverse correctement les frontières de jour.
  let sessionRows = $derived.by((): SessionRow[] => {
    const bySession = new Map<number, FeedResponse[]>();
    for (const feed of feeds) {
      if (!bySession.has(feed.sessionId)) bySession.set(feed.sessionId, []);
      bySession.get(feed.sessionId)!.push(feed);
    }

    const sessions = Array.from(bySession.entries())
      .map(([sessionId, sessionFeeds]) => {
        const sorted = [...sessionFeeds].sort((a, b) => a.startTime.localeCompare(b.startTime));
        const latestEnd = sessionFeeds.reduce(
          (latest, f) => (f.endTime && f.endTime > latest ? f.endTime : latest),
          sessionFeeds[0].endTime ?? sessionFeeds[0].startTime
        );
        return {
          sessionId,
          startTime: sorted[0].startTime,
          endTime: latestEnd,
          feeds: sorted,
          dayKey: dayKey(sorted[0].startTime),
        };
      })
      .sort((a, b) => a.startTime.localeCompare(b.startTime)); // chronologique croissant

    return sessions.map((session, i) => ({
      ...session,
      delaySincePrevious:
        i === 0 ? null : new Date(session.startTime).getTime() - new Date(sessions[i - 1].endTime).getTime(),
    }));
  });

  // Regroupe par jour pour l'affichage, en conservant l'ordre décroissant (plus récent en premier)
  interface DayGroup {
    key: string;
    rows: SessionRow[];
    totalMinutes: number;
    count: number;
  }

  let groups = $derived.by((): DayGroup[] => {
    const byDay = new Map<string, SessionRow[]>();
    for (const row of sessionRows) {
      if (!byDay.has(row.dayKey)) byDay.set(row.dayKey, []);
      byDay.get(row.dayKey)!.push(row);
    }

    return Array.from(byDay.entries())
      .map(([key, rows]) => {
        const allFeedsOfDay = rows.flatMap((r) => r.feeds);
        return {
          key,
          rows: [...rows].reverse(), // plus récent en premier au sein du jour
          totalMinutes: allFeedsOfDay.reduce((sum, f) => sum + (f.durationMinutes ?? 0), 0),
          count: allFeedsOfDay.length,
        };
      })
      .sort((a, b) => b.key.localeCompare(a.key)); // jours les plus récents en premier
  });
</script>

<div class="history">
  <h2>Historique</h2>

  {#if groups.length === 0}
    <div class="empty">Aucune tétée enregistrée pour l'instant.</div>
  {:else}
    {#each groups as group (group.key)}
      <div class="day-group">
        <div class="day-heading">
          <span>{dayHeading(group.key)}</span>
          <span>{group.count} tétées · {group.totalMinutes} min</span>
        </div>
        {#each group.rows as session (session.sessionId)}
          {#if session.delaySincePrevious !== null}
            <div class="delay-divider">
              <span>{fmtDelay(session.delaySincePrevious)} depuis la tétée précédente</span>
            </div>
          {/if}
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
  .history h2 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 16px;
    margin: 0 0 12px;
  }
  .day-group { margin-bottom: 20px; }
  .day-heading {
    font-size: 12.5px;
    color: var(--muted);
    margin-bottom: 6px;
    display: flex;
    justify-content: space-between;
  }
  .delay-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    color: var(--muted);
    padding: 4px 0;
    gap: 6px;
  }
  .delay-divider::before,
  .delay-divider::after {
    content: "";
    flex: 1;
    height: 1px;
    background: var(--surface-border);
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