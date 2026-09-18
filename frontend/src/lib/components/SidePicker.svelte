<script lang="ts">
  import type { BreastSide } from "#lib/types/feed.ts";

  let {
    selected = $bindable(null),
    disabled = false,
  }: {
    selected: BreastSide | null;
    disabled?: boolean;
  } = $props();

  function toggle(breastSide: BreastSide) {
    if (disabled) return;
    selected = selected === breastSide ? null : breastSide;
  }
</script>

<div class="side-picker">
  <button
    class="side-btn left"
    class:active={selected === "LEFT"}
    {disabled}
    onclick={() => toggle("LEFT")}
  >
    Gauche
  </button>
  <button
    class="side-btn right"
    class:active={selected === "RIGHT"}
    {disabled}
    onclick={() => toggle("RIGHT")}
  >
    Droite
  </button>
</div>

<style>
  .side-picker {
    display: flex;
    gap: 10px;
    width: 100%;
    max-width: 380px;
    margin-bottom: 28px;
  }
  .side-btn {
    flex: 1;
    padding: 13px 0;
    border-radius: 999px;
    border: 1.5px solid var(--surface-border);
    background: var(--surface);
    color: var(--muted);
    font-family: "Inter", sans-serif;
    font-weight: 500;
    font-size: 15px;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  .side-btn.left.active {
    background: var(--left-soft);
    border-color: var(--left);
    color: var(--left);
  }
  .side-btn.right.active {
    background: var(--right-soft);
    border-color: var(--right);
    color: var(--right);
  }
  .side-btn:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }
</style>