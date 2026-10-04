<script lang="ts">
    import { authentication } from "#lib/stores/auth.svelte.ts";

    let username = $state(authentication.username)
</script>

<section>
    <h2>Bienvenue <span class="username">{username}</span></h2>
</section>

<style>
 section {
    display: flex;
    justify-content: center;
    padding: 2rem 0;
    max-width: 420px;
    margin: 0 auto;
    width: 100%;
    box-sizing: border-box;
}
h2 {
    display: flex;
    justify-content: flex-start;
    font-weight: 500;
    font-size: 20px;
    margin: 0;
    width: 100%;
    color: var(--text);
}
.username {
    padding-left: 0.5rem;
    color:  var(--accent);
}
</style>