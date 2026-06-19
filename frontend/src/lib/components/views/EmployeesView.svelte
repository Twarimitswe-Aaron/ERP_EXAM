<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';

  let employees = $state([]);
  let loading = $state(true);

  onMount(async () => {
    try {
      employees = await api.get('/employees');
    } catch (e) {
      console.error(e);
    } finally {
      loading = false;
    }
  });
</script>

<Card title="Manage Employees">
  <div class="mb-4">
    <Button>Add New Employee</Button>
  </div>
  {#if loading}
    <p>Loading...</p>
  {:else}
    <div class="overflow-x-auto">
      <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200">
          <tr>
            <th class="px-6 py-4">Name</th>
            <th class="px-6 py-4">Email</th>
            <th class="px-6 py-4">Department</th>
          </tr>
        </thead>
        <tbody>
          {#each employees as emp}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4">{emp.firstName} {emp.lastName}</td>
              <td class="px-6 py-4">{emp.email}</td>
              <td class="px-6 py-4">{emp.department?.name || '-'}</td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>
