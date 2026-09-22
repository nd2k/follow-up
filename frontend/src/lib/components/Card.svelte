<script lang="ts">
  let {
    title = null,
    children,
  }: {
    title?: string | null;
    children: () => any;
  } = $props();
</script>

<section class="card">
  {#if title}
    <h2>{title}</h2>
  {/if}
  {@render children()}
</section>

<style>
  .card {
    width: 100%;
    background: var(--surface);
    border: 1px solid var(--surface-border);
    border-radius: var(--card-radius);
    box-shadow: var(--card-shadow);
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 2rem 0;
  }
  h2 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 16px;
    margin: 0 0 16px;
    align-self: flex-start;
    color: var(--text);
  }
</style>