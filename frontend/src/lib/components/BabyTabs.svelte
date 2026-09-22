<script lang="ts">
  import { babyStore } from "#lib/stores/selectedBaby.svelte.ts";
</script>

{#if babyStore.babies.length > 0}
  <nav class="tabs">
    {#each babyStore.babies as baby (baby.id)}
      <button
        class="tab"
        class:active={babyStore.selectedId === baby.id}
        onclick={() => babyStore.select(baby.id)}
      >
        {baby.name}
      </button>
    {/each}
  </nav>
{/if}

<style>
  .tabs {
    display: flex;
    gap: 6px;
    padding: 0 20px 16px;
    max-width: 420px;
    margin: 0 auto;
    width: 100%;
    box-sizing: border-box;
    overflow-x: auto;
  }
  .tab {
    flex-shrink: 0;
    padding: 8px 16px;
    border-radius: 999px;
    border: 1.5px solid var(--surface-border);
    background: var(--surface);
    color: var(--muted);
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    white-space: nowrap;
  }
  .tab.active {
    background: var(--accent);
    border-color: var(--accent);
    color: #fff;
  }
</style>