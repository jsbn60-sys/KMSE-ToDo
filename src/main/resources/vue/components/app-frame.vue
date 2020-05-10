<template id="app-frame">
    <div class="app-frame">
        <header>
            <span>KMS Tasks app</span>
        </header>
        <slot></slot>
    </div>
</template>
<script>
    Vue.component("app-frame", {template: "#app-frame"});
</script>
<style>
    html {
        font-family: sans-serif;
    }
    .app-frame > header {
        padding: 20px;
        background: #b6e2ff;
        font-size: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }
</style>
