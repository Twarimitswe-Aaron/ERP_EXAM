<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { getAuthToken } from '$lib/api';

  onMount(() => {
    const token = getAuthToken();
    if (token) {
      goto('/dashboard');
    } else {
      goto('/login');
    }
  });
</script>

<div class="min-h-screen flex items-center justify-center bg-gray-50">
  <p class="text-gray-500">Redirecting...</p>
</div>
