<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';
  import Input from '$lib/components/Input.svelte';

  let admins = $state<any[]>([]);
  let institutions = $state<any[]>([]);
  let loading = $state(true);
  let error = $state('');

  // Form State
  let showForm = $state(false);
  let firstName = $state('');
  let lastName = $state('');
  let email = $state('');
  let password = $state('');
  let institutionId = $state('');
  let formError = $state('');
  let saving = $state(false);

  async function loadData() {
    try {
      loading = true;
      const [adminsRes, instRes] = await Promise.all([
        api.get('/admins'),
        api.get('/institutions')
      ]);
      admins = Array.isArray(adminsRes) ? adminsRes : [];
      institutions = Array.isArray(instRes) ? instRes : [];
    } catch (e: any) {
      error = "Could not load data.";
    } finally {
      loading = false;
    }
  }

  onMount(() => {
    loadData();
  });

  async function handleCreate() {
    if (!firstName || !lastName || !email || !password || !institutionId) {
      formError = "All fields are required.";
      return;
    }

    saving = true;
    formError = '';
    
    try {
      await api.post('/admins', { firstName, lastName, email, password, institutionId });
      showForm = false;
      firstName = ''; lastName = ''; email = ''; password = ''; institutionId = '';
      await loadData();
    } catch (e: any) {
      formError = e.message || "Failed to create admin.";
    } finally {
      saving = false;
    }
  }
</script>

<Card>
  <div class="flex justify-between items-center mb-6">
    <h2 class="text-xl font-bold text-gray-800">Institution Admins</h2>
    <Button onclick={() => showForm = !showForm}>
      {showForm ? 'Cancel' : '+ New Admin'}
    </Button>
  </div>

  {#if showForm}
    <div class="mb-6 p-4 bg-gray-50 border border-gray-200 rounded-md">
      <h3 class="text-lg font-semibold text-gray-700 mb-4">Create New Admin</h3>
      {#if formError}
        <div class="text-red-500 text-sm mb-4">{formError}</div>
      {/if}
      <form onsubmit={(e) => { e.preventDefault(); handleCreate(); }}>
        <div class="grid grid-cols-2 gap-4">
          <Input id="admin_fname" label="First Name" bind:value={firstName} required />
          <Input id="admin_lname" label="Last Name" bind:value={lastName} required />
        </div>
        <Input id="admin_email" type="email" label="Email Address" bind:value={email} required />
        <Input id="admin_pwd" type="password" label="Password" bind:value={password} required />
        
        <div class="mb-4">
          <label for="admin_inst" class="block text-sm font-medium text-gray-700 mb-1">Assign to Institution</label>
          <select id="admin_inst" bind:value={institutionId} class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-rwanda-blue focus:border-transparent bg-white" required>
            <option value="" disabled selected>Select an Institution</option>
            {#each institutions as inst}
              <option value={inst.id}>{inst.name}</option>
            {/each}
          </select>
        </div>

        <div class="mt-6 flex justify-end">
          <Button type="submit" disabled={saving}>
            {saving ? 'Saving...' : 'Save Admin'}
          </Button>
        </div>
      </form>
    </div>
  {/if}

  {#if loading}
    <p>Loading admins...</p>
  {:else if error}
    <p class="text-red-500">{error}</p>
  {:else if admins.length === 0}
    <p class="text-gray-500">No admins found.</p>
  {:else}
    <div class="overflow-x-auto">
      <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200">
          <tr>
            <th class="px-6 py-4">Email</th>
            <th class="px-6 py-4">Institution</th>
          </tr>
        </thead>
        <tbody>
          {#each admins as admin}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4 font-medium">{admin.email}</td>
              <td class="px-6 py-4 text-gray-600">{admin.institution ? admin.institution.name : 'N/A'}</td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>
