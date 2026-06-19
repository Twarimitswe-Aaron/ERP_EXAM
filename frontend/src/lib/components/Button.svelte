<script lang="ts">
  let { type = 'button', class: className = '', onclick = undefined, disabled = false, children } = $props<{
    type?: 'button' | 'submit' | 'reset';
    class?: string;
    onclick?: () => void;
    disabled?: boolean;
    children?: import('svelte').Snippet;
  }>();
</script>

<button
  {type}
  {onclick}
  {disabled}
  class="inline-flex justify-center items-center px-4 py-2 bg-rwanda-blue text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed {className}"
>
  {#if children}
    {@render children()}
  {/if}
</button>
