<script lang="ts">
  import { slide } from 'svelte/transition';

  let { 
    id, 
    type = 'text', 
    label, 
    value = $bindable(), 
    required = false, 
    error = '',
    class: className = '' 
  } = $props<{
    id: string;
    type?: string;
    label: string;
    value?: string | number;
    required?: boolean;
    error?: string;
    class?: string;
  }>();
</script>

<div class="mb-4 {className}">
  <label for={id} class="block text-sm font-medium text-gray-700 mb-1">{label}</label>
  <input
    {id}
    {type}
    bind:value
    {required}
    class="w-full px-3 py-2 border {error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-rwanda-blue'} rounded-md focus:outline-none focus:ring-2 focus:border-transparent transition-colors bg-white"
  />
  {#if error}
    <p transition:slide={{ duration: 200 }} class="mt-1 text-sm text-red-500">{error}</p>
  {/if}
</div>
