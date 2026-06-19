<script lang="ts">
  let { title = '', children, class: className = '' } = $props<{
    title?: string;
    children?: import('svelte').Snippet;
    class?: string;
  }>();
</script>

<div class="bg-white border border-gray-200 rounded-md p-6 {className}">
  {#if title}
    <h2 class="text-xl font-bold text-gray-800 mb-4">{title}</h2>
  {/if}
  {#if children}
    {@render children()}
  {/if}
</div>
