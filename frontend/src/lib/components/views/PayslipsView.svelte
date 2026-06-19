<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  
  let payslips = $state<any[]>([]);
  let loading = $state(true);
  let error = $state('');
  
  onMount(async () => {
    try {
      const res = await api.get('/payroll/my-payslips');
      payslips = Array.isArray(res) ? res : [];
    } catch (e: any) {
      console.error(e);
      error = "Could not load payslips. Please check if your account is fully set up.";
    } finally {
      loading = false;
    }
  });
</script>

<Card title="My Payslips">
  {#if loading}
    <p>Loading payslips...</p>
  {:else if error}
    <p class="text-red-500">{error}</p>
  {:else if payslips.length === 0}
    <p class="text-gray-500">You have no payslips.</p>
  {:else}
    <div class="overflow-x-auto">
      <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200">
          <tr>
            <th class="px-6 py-4">Month/Year</th>
            <th class="px-6 py-4">Gross Salary</th>
            <th class="px-6 py-4">Net Salary</th>
            <th class="px-6 py-4">Status</th>
          </tr>
        </thead>
        <tbody>
          {#each payslips as p}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4">{p.payrollMonth}/{p.payrollYear}</td>
              <td class="px-6 py-4">{p.grossSalary}</td>
              <td class="px-6 py-4">{p.netSalary}</td>
              <td class="px-6 py-4">
                <span class="px-2 py-1 bg-green-100 text-green-800 rounded-md">{p.status}</span>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>
