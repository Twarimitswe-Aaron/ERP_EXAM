<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';
  import Input from '$lib/components/Input.svelte';

  let admins = $state<any[]>([]);
  let institutions = $state<any[]>([]);
  let employees = $state<any[]>([]); // We need employees to map user emails to salaries
  let loading = $state(true);
  let error = $state('');

  // Form State
  let showForm = $state(false);
  let firstName = $state('');
  let lastName = $state('');
  let email = $state('');
  let password = $state('');
  let institutionId = $state('');
  let baseSalary = $state('');
  let formError = $state('');
  let saving = $state(false);

  // Edit Salary State
  let editingAdminId = $state('');
  let editSalaryAmount = $state('');
  
  // Payroll State
  let payrollMonth = $state(new Date().getMonth() + 1);
  let payrollYear = $state(new Date().getFullYear());
  let generating = $state(false);
  let payrollMessage = $state('');

  async function loadData() {
    try {
      loading = true;
      const [adminsRes, instRes, empRes] = await Promise.all([
        api.get('/admins'),
        api.get('/institutions'),
        api.get('/employees')
      ]);
      admins = Array.isArray(adminsRes) ? adminsRes : [];
      institutions = Array.isArray(instRes) ? instRes : [];
      employees = Array.isArray(empRes) ? empRes : [];
    } catch (e: any) {
      error = "Could not load data.";
    } finally {
      loading = false;
    }
  }

  onMount(() => {
    loadData();
  });

  function getAdminSalary(email: string) {
    const emp = employees.find(e => e.email === email);
    return emp && emp.baseSalary ? emp.baseSalary.toLocaleString() : '-';
  }

  async function handleCreate() {
    if (!firstName || !lastName || !email || !password || !institutionId || !baseSalary) {
      formError = "All fields are required.";
      return;
    }

    saving = true;
    formError = '';
    
    try {
      await api.post('/admins', { 
        firstName, lastName, email, password, institutionId, baseSalary: Number(baseSalary) 
      });
      showForm = false;
      firstName = ''; lastName = ''; email = ''; password = ''; institutionId = ''; baseSalary = '';
      await loadData();
    } catch (e: any) {
      formError = e.message || "Failed to create admin.";
    } finally {
      saving = false;
    }
  }

  async function handleUpdateSalary(adminId: string) {
    if (!editSalaryAmount) return;
    try {
      await api.put(`/admins/${adminId}/salary`, { baseSalary: Number(editSalaryAmount) });
      editingAdminId = '';
      await loadData();
    } catch (e: any) {
      alert("Failed to update salary.");
    }
  }

  async function handleGenerateAdminPayroll() {
    generating = true;
    payrollMessage = '';
    try {
      await api.post('/payroll/generate/admins', { month: payrollMonth, year: payrollYear });
      payrollMessage = "Successfully generated payslips for all Institution Admins!";
    } catch (e: any) {
      payrollMessage = "Error: " + (e.message || "Could not generate payroll.");
    } finally {
      generating = false;
    }
  }
</script>

<div class="mb-6 bg-white p-6 rounded-lg shadow-sm border border-gray-200">
  <div class="flex justify-between items-center">
    <div>
      <h2 class="text-xl font-bold text-gray-800">Generate Admin Payrolls</h2>
      <p class="text-sm text-gray-500 mt-1">Process payroll specifically for Institution Admins across all institutions.</p>
    </div>
    <div class="flex space-x-4 items-center">
      <div class="flex flex-col">
        <label for="month" class="text-xs font-medium text-gray-700">Month</label>
        <input id="month" type="number" min="1" max="12" bind:value={payrollMonth} class="w-20 px-3 py-1.5 border border-gray-300 rounded-md text-sm" />
      </div>
      <div class="flex flex-col">
        <label for="year" class="text-xs font-medium text-gray-700">Year</label>
        <input id="year" type="number" min="2020" max="2100" bind:value={payrollYear} class="w-24 px-3 py-1.5 border border-gray-300 rounded-md text-sm" />
      </div>
      <div class="flex flex-col justify-end mt-4">
        <Button onclick={handleGenerateAdminPayroll} disabled={generating} class="!bg-green-600 hover:!bg-green-700">
          {generating ? 'Processing...' : 'Generate Pay'}
        </Button>
      </div>
    </div>
  </div>
  {#if payrollMessage}
    <div class="mt-4 p-3 {payrollMessage.includes('Error') ? 'bg-red-50 text-red-800' : 'bg-green-50 text-green-800'} rounded-md text-sm font-medium">
      {payrollMessage}
    </div>
  {/if}
</div>

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
        <div class="grid grid-cols-2 gap-4">
          <Input id="admin_email" type="email" label="Email Address" bind:value={email} required />
          <Input id="admin_pwd" type="password" label="Password" bind:value={password} required />
        </div>
        
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label for="admin_inst" class="block text-sm font-medium text-gray-700 mb-1">Assign to Institution</label>
            <select id="admin_inst" bind:value={institutionId} class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-rwanda-blue focus:border-transparent bg-white" required>
              <option value="" disabled selected>Select an Institution</option>
              {#each institutions as inst}
                <option value={inst.id}>{inst.name}</option>
              {/each}
            </select>
          </div>
          <Input id="admin_salary" type="number" label="Base Salary (RWF)" bind:value={baseSalary} required />
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
            <th class="px-6 py-4">Base Salary</th>
            <th class="px-6 py-4">Action</th>
          </tr>
        </thead>
        <tbody>
          {#each admins as admin}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4 font-medium">{admin.email}</td>
              <td class="px-6 py-4 text-gray-600">{admin.institution ? admin.institution.name : 'N/A'}</td>
              <td class="px-6 py-4 font-mono">
                {#if editingAdminId === admin.id}
                  <input type="number" bind:value={editSalaryAmount} class="w-32 px-2 py-1 border border-gray-300 rounded" placeholder="New Salary">
                {:else}
                  {getAdminSalary(admin.email)}
                {/if}
              </td>
              <td class="px-6 py-4">
                {#if editingAdminId === admin.id}
                  <div class="flex space-x-2">
                    <Button onclick={() => handleUpdateSalary(admin.id)} class="!py-1 !px-3 text-xs !bg-green-600">Save</Button>
                    <Button onclick={() => editingAdminId = ''} class="!py-1 !px-3 text-xs bg-gray-100 !text-gray-800">Cancel</Button>
                  </div>
                {:else}
                  <Button onclick={() => { editingAdminId = admin.id; editSalaryAmount = ''; }} class="!py-1 !px-3 text-xs bg-gray-100 !text-gray-800 hover:bg-gray-200">Edit Salary</Button>
                {/if}
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>
