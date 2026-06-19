<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';
  import Input from '$lib/components/Input.svelte';

  let employees = $state<any[]>([]);
  let loading = $state(true);

  // Modal State
  let editingEmp: any = $state(null);
  let department = $state('');
  let position = $state('');
  let joiningDate = $state(new Date().toISOString().split('T')[0]);
  let baseSalary = $state<number | ''>('');
  let status = $state('Active');
  
  let saving = $state(false);
  let formError = $state('');

  const departments = ['IT', 'FINANCE', 'HR', 'OPERATIONS', 'SALES', 'EXECUTIVE'];
  const positions = ['MANAGER', 'ENGINEER', 'ACCOUNTANT', 'ANALYST', 'DIRECTOR', 'SUPPORT_STAFF'];

  async function loadEmployees() {
    try {
      loading = true;
      const res = await api.get('/employees');
      employees = Array.isArray(res) ? res : [];
    } catch (e) {
      console.error(e);
    } finally {
      loading = false;
    }
  }

  onMount(() => {
    loadEmployees();
  });

  function openEdit(emp: any) {
    editingEmp = emp;
    department = emp.employment?.department || '';
    position = emp.employment?.position || '';
    joiningDate = emp.employment?.joiningDate || new Date().toISOString().split('T')[0];
    baseSalary = emp.baseSalary || '';
    status = emp.status || 'Active';
    formError = '';
  }

  function closeEdit() {
    editingEmp = null;
  }

  async function handleSave() {
    if (!department || !position || baseSalary === '') {
      formError = "Department, Position, and Base Salary are required.";
      return;
    }

    saving = true;
    formError = '';

    const payload = {
      department,
      position,
      joiningDate,
      baseSalary: Number(baseSalary),
      status
    };

    try {
      await api.put(`/employees/${editingEmp.id}/employment`, payload);
      closeEdit();
      await loadEmployees();
    } catch (e: any) {
      formError = e.message || "Failed to update employment details.";
    } finally {
      saving = false;
    }
  }
</script>

<Card title="Manage Employees">
  {#if loading}
    <p>Loading...</p>
  {:else}
    <div class="overflow-x-auto">
      <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200">
          <tr>
            <th class="px-6 py-4">Name</th>
            <th class="px-6 py-4">Department / Position</th>
            <th class="px-6 py-4">Base Salary</th>
            <th class="px-6 py-4">Status</th>
            <th class="px-6 py-4">Action</th>
          </tr>
        </thead>
        <tbody>
          {#each employees as emp}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4">
                <div class="font-medium">{emp.firstName} {emp.lastName}</div>
                <div class="text-xs text-gray-500">{emp.email}</div>
              </td>
              <td class="px-6 py-4">
                {#if emp.employment?.department}
                  <span class="block">{emp.employment.department}</span>
                  <span class="block text-xs text-gray-500">{emp.employment.position}</span>
                {:else}
                  <span class="text-red-500 text-xs font-semibold">Unassigned</span>
                {/if}
              </td>
              <td class="px-6 py-4 font-mono">{emp.baseSalary ? `${emp.baseSalary.toLocaleString()}` : '-'}</td>
              <td class="px-6 py-4">
                {#if emp.status === 'Active'}
                  <span class="px-2 py-1 bg-green-100 text-green-800 rounded-md text-xs">Active</span>
                {:else}
                  <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-md text-xs">{emp.status || 'Pending'}</span>
                {/if}
              </td>
              <td class="px-6 py-4">
                <Button onclick={() => openEdit(emp)} class="!py-1 !px-3 text-xs bg-gray-100 !text-gray-800 hover:bg-gray-200">Configure</Button>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>

{#if editingEmp}
  <div class="fixed inset-0 bg-black/50 bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-md shadow-lg max-w-md w-full p-6">
      <h3 class="text-lg font-bold text-gray-900 mb-4">Configure Employment</h3>
      <p class="text-sm text-gray-600 mb-4">Editing details for <span class="font-semibold">{editingEmp.firstName} {editingEmp.lastName}</span></p>
      
      {#if formError}
        <div class="bg-red-50 text-red-600 p-3 rounded-md mb-4 text-sm">{formError}</div>
      {/if}

      <form onsubmit={(e) => { e.preventDefault(); handleSave(); }}>
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label for="dept" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
            <select id="dept" bind:value={department} class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-rwanda-blue bg-white" required>
              <option value="" disabled>Select Dept</option>
              {#each departments as d}
                <option value={d}>{d}</option>
              {/each}
            </select>
          </div>
          <div>
            <label for="pos" class="block text-sm font-medium text-gray-700 mb-1">Position</label>
            <select id="pos" bind:value={position} class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-rwanda-blue bg-white" required>
              <option value="" disabled>Select Position</option>
              {#each positions as p}
                <option value={p}>{p}</option>
              {/each}
            </select>
          </div>
        </div>

        <div class="mb-4">
          <Input id="salary" type="number" label="Base Salary (RWF)" bind:value={baseSalary as number} required />
        </div>

        <div class="grid grid-cols-2 gap-4 mb-6">
          <Input id="join" type="date" label="Joining Date" bind:value={joiningDate} required />
          <div>
            <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select id="status" bind:value={status} class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-rwanda-blue bg-white" required>
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
            </select>
          </div>
        </div>

        <div class="flex justify-end space-x-3 mt-6 border-t pt-4">
          <Button type="button" onclick={closeEdit} class="!bg-white !text-gray-700 border border-gray-300 hover:!bg-gray-50">Cancel</Button>
          <Button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save Changes'}</Button>
        </div>
      </form>
    </div>
  </div>
{/if}
