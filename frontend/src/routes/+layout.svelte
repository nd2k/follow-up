<script lang="ts">
    import '../app.css';
	import favicon from '#lib/assets/favicon.svg';
    import { authentication } from '#lib/stores/auth.svelte.ts';
	import { page } from '$app/state';

    import AppHeader from '#lib/components/AppHeader.svelte';
    import BabiesTab from '#lib/components/BabiesTab.svelte';
	import { onMount } from 'svelte';
	import { theme } from '#lib/stores/theme.svelte.ts';
	import { goto } from '$app/navigation';
	import { checkHealth } from '#lib/services/healthService.ts';
	import AppHero from '#lib/components/AppHero.svelte';
    import { babyStore } from '#lib/stores/baby.svelte.ts';

    let { children } = $props();
    let serverIsUp = $state(false);
    let appIsReady = $derived(serverIsUp && authentication.status === "AUTHENTICATED" && page.url.pathname !== "/login");
    let keepAliveInterval: ReturnType<typeof setInterval> | undefined;
    let babiesAreLoaded = false;

    onMount(() => {
        theme.init();
        (async() => {
            await authentication.init();
            serverIsUp = true;
            if (authentication.status === "AUTHENTICATED") {
                await babyStore.refresh();
            }
        })();
        keepAliveInterval = setInterval(() => {
            checkHealth().catch(() => {});
        }, 10 * 60 * 1000);
        return () => clearInterval(keepAliveInterval);
    })

    $effect(() => {
        if (authentication.status === "AUTHENTICATED" && !babiesAreLoaded) {
            babiesAreLoaded = true;
            babyStore.refresh();
        } else if (authentication.status === "UNAUTHENTICATED") {
            babiesAreLoaded = false;
        }
    });

    $effect(() => {
        if (!serverIsUp) return;
        const isLoginPage = page.url.pathname === "/login";
        if (authentication.status === "UNAUTHENTICATED" && !isLoginPage) {
            goto("/login");
        } else if (authentication.status === "AUTHENTICATED" && isLoginPage) {
            goto("/feeds");
        }
    })
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

{#if serverIsUp}
    {#if appIsReady}
        <AppHeader />
        <AppHero />      
        <BabiesTab />  
    {/if}
    {@render children()}
{:else}
   <div class="boot-loader">
    <div class="spinner"></div>
    <p>Chargement…</p>
  </div>
{/if}

<style>
.boot-loader {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--bg);
  padding-top: env(safe-area-inset-top, 0px);
  padding-bottom: env(safe-area-inset-bottom, 0px);
}

.spinner {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 3px solid var(--surface-border);
  border-top-color: var(--accent);
  animation: spin 0.7s linear infinite;
}

.boot-loader p {
  font-family: "Fraunces", serif;
  font-weight: 500;
  font-size: 14px;
  color: var(--muted);
  margin: 0;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>

