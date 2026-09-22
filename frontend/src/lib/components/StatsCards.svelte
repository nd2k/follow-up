<script lang="ts">
  import type { StatsResponse } from "#lib/types/feed.ts";

  let { stats }: { stats: StatsResponse | null } = $props();
</script>

<div class="stats">
  <div class="stats-row">
    <div class="stat-card">
      <div class="stat-value">{stats?.count ?? 0}</div>
      <div class="stat-label">tétées aujourd'hui</div>
    </div>
    <div class="stat-card">
      <div class="stat-value">{stats?.totalDurationInMinutes ?? 0}</div>
      <div class="stat-label">minutes aujourd'hui</div>
    </div>
    <div class="stat-card">
      <div class="stat-value">{stats?.averageInMinutes ?? 0}</div>
      <div class="stat-label">min / tétée (moy.)</div>
    </div>
  </div>
</div>

<style>
  .stats {
    width: 100%;
  }
  .stats-row {
    display: flex;
    gap: 12px;
  }
  .stat-card {
    flex: 1;
    background: var(--surface);
    border: 1px solid var(--surface-border);
    border-radius: 14px;
    padding: 14px 16px;
  }
  .stat-value {
    font-family: "Fraunces", serif;
    font-size: 26px;
    font-weight: 500;
  }
  .stat-label {
    font-size: 12.5px;
    color: var(--muted);
    margin-top: 2px;
  }
</style>