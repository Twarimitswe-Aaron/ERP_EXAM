<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  
  let messages = $state<any[]>([]);
  let loading = $state(true);
  let error = $state('');
  
  onMount(async () => {
    try {
      const res = await api.get('/messages/my-messages');
      messages = Array.isArray(res) ? res : [];
    } catch (e: any) {
      console.error(e);
      error = "Could not load messages.";
    } finally {
      loading = false;
    }
  });
</script>

<Card title="My Messages">
  {#if loading}
    <p>Loading messages...</p>
  {:else if error}
    <p class="text-red-500">{error}</p>
  {:else if messages.length === 0}
    <p class="text-gray-500">You have no messages.</p>
  {:else}
    <div class="space-y-4">
      {#each messages as msg}
        <div class="p-4 border border-gray-200 rounded-md bg-gray-50">
          <p class="text-sm text-gray-800">{msg.text}</p>
          <p class="text-xs text-gray-500 mt-2">{new Date(msg.dateTime).toLocaleString()}</p>
        </div>
      {/each}
    </div>
  {/if}
</Card>
