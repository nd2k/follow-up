<script lang="ts">
	import '../app.css';
	import favicon from '#lib/assets/favicon.svg';
	import { checkHealth } from "#lib/services/healthService.ts";
	import { auth } from '#lib/stores/auth.svelte.ts';
	import { onMount } from 'svelte';
	import { goto } from "$app/navigation";
  	import { page } from "$app/state";
	import { theme } from "#lib/stores/theme.svelte.ts";
	import AppHeader from '#lib/components/AppHeader.svelte';
	import BabyTabs from '#lib/components/BabyTabs.svelte';

	let { children } = $props();
	let ready = $state(false);
	let keepAliveInterval: ReturnType<typeof setInterval> | undefined;

	onMount(() => {
		theme.init();
		(async () => {
		await auth.init();
		ready = true;
		})();
		keepAliveInterval = setInterval(() => {
		checkHealth().catch(() => {});
		}, 10 * 60 * 1000);
		return () => clearInterval(keepAliveInterval);
	});

	$effect(() => {
		if (!ready) return;
		const isLoginPage = page.url.pathname === "/login";
		if (auth.status === "unauthenticated" && !isLoginPage) {
		goto("/login");
		} else if (auth.status === "authenticated" && isLoginPage) {
		goto("/feeds");
		}
	});

	let showChrome = $derived(ready && auth.status === "authenticated" && page.url.pathname !== "/login");
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

{#if ready}
{#if showChrome}
    <AppHeader />
    <BabyTabs />
  {/if}
  {@render children()}
{:else}
  <div class="boot-loader">Chargement…</div>
{/if}
