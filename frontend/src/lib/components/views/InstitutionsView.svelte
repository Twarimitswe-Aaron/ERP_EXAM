<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';
  import Input from '$lib/components/Input.svelte';

  let institutions = $state<any[]>([]);
  let loading = $state(true);
  let error = $state('');

  // Form State
  let showForm = $state(false);
  let name = $state('');
  let formError = $state('');
  let saving = $state(false);

  async function loadInstitutions() {
    try {
      loading = true;
      const res = await api.get('/institutions');
      institutions = Array.isArray(res) ? res : [];
    } catch (e: any) {
      error = "Could not load institutions.";
    } finally {
      loading = false;
    }
  }

  onMount(() => {
    loadInstitutions();
  });

  async function handleCreate() {
    if (!name.trim()) {
      formError = "Institution name is required.";
      return;
    }

    saving = true;
    formError = '';
    
    try {
      await api.post('/institutions', { name });
      showForm = false;
      name = '';
      await loadInstitutions();
    } catch (e: any) {
      formError = e.message || "Failed to create institution.";
    } finally {
      saving = false;
    }
  }
</script>

<Card>
  <div class="flex justify-between items-center mb-6">
    <h2 class="text-xl font-bold text-gray-800">Institutions</h2>
    <Button onclick={() => showForm = !showForm}>
      {showForm ? 'Cancel' : '+ New Institution'}
    </Button>
  </div>

  {#if showForm}
    <div class="mb-6 p-4 bg-gray-50 border border-gray-200 rounded-md">
      <h3 class="text-lg font-semibold text-gray-700 mb-4">Create New Institution</h3>
      {#if formError}
        <div class="text-red-500 text-sm mb-4">{formError}</div>
      {/if}
      <form onsubmit={(e) => { e.preventDefault(); handleCreate(); }}>
        <Input id="inst_name" label="Institution Name" bind:value={name} required />
        <div class="mt-4 flex justify-end">
          <Button type="submit" disabled={saving}>
            {saving ? 'Saving...' : 'Save Institution'}
          </Button>
        </div>
      </form>
    </div>
  {/if}

  {#if loading}
    <p>Loading institutions...</p>
  {:else if error}
    <p class="text-red-500">{error}</p>
  {:else if institutions.length === 0}
    <p class="text-gray-500">No institutions found.</p>
  {:else}
    <div class="overflow-x-auto">
      <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200">
          <tr>
            <th class="px-6 py-4">ID</th>
            <th class="px-6 py-4">Name</th>
          </tr>
        </thead>
        <tbody>
          {#each institutions as inst}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4 font-mono text-xs text-gray-500">{inst.id}</td>
              <td class="px-6 py-4 font-medium">{inst.name}</td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>
