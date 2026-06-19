<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { getAuthToken, removeAuthToken } from '$lib/api';
  import Button from '$lib/components/Button.svelte';
  import Card from '$lib/components/Card.svelte';

  import EmployeesView from '$lib/components/views/EmployeesView.svelte';
  import PayrollView from '$lib/components/views/PayrollView.svelte';
  import PayslipsView from '$lib/components/views/PayslipsView.svelte';
  import MessagesView from '$lib/components/views/MessagesView.svelte';
  import InstitutionsView from '$lib/components/views/InstitutionsView.svelte';
  import AdminsView from '$lib/components/views/AdminsView.svelte';

  let role = $state<string | null>(null);
  let userEmail = $state<string | null>(null);

  onMount(() => {
    const token = getAuthToken();
    if (!token) {
      goto('/login');
      return;
    }

    try {
      const payloadBase64 = token.split('.')[1];
      const payloadDecoded = atob(payloadBase64);
      const payload = JSON.parse(payloadDecoded);
      
      role = payload.role;
      userEmail = payload.sub;
    } catch (e) {
      removeAuthToken();
      goto('/login');
    }
  });

  function handleLogout() {
    removeAuthToken();
    goto('/login');
  }
</script>

<div class="min-h-screen bg-gray-50 flex flex-col">
  <nav class="bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
    <div class="text-xl font-bold text-rwanda-blue">Gov ERP Payroll</div>
    <div class="flex items-center space-x-4">
      <span class="text-sm text-gray-600">{userEmail} ({role})</span>
      <Button onclick={handleLogout} class="!bg-gray-100 !text-gray-800 hover:!bg-gray-200">Logout</Button>
    </div>
  </nav>

  <main class="flex-1 p-6 max-w-7xl mx-auto w-full">
    {#if !role}
      <div class="flex justify-center py-12">Loading...</div>
    {:else}
      {#if role === 'SUPER_ADMIN'}
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Super Admin Dashboard</h1>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 items-start">
           <InstitutionsView />
           <AdminsView />
        </div>
      {:else if role === 'ADMIN'}
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Institution Dashboard</h1>
        <div class="grid grid-cols-1 gap-6">
           <EmployeesView />
           <PayrollView />
        </div>
      {:else if role === 'EMPLOYEE'}
        <h1 class="text-2xl font-bold text-gray-800 mb-6">Employee Portal</h1>
        <div class="grid grid-cols-1 gap-6">
           <PayslipsView />
           <MessagesView />
        </div>
      {/if}
    {/if}
  </main>
</div>
