<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';
  
  let payslips = $state<any[]>([]);
  let loading = $state(true);
  let error = $state('');

  // Modal State
  let selectedPayslip: any = $state(null);
  
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

  function formatCurrency(val: number | undefined | null) {
    if (val === undefined || val === null) return '0';
    return val.toLocaleString();
  }

  function openDetails(p: any) {
    selectedPayslip = p;
  }

  function closeDetails() {
    selectedPayslip = null;
  }
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
            <th class="px-6 py-4">Gross Salary (RWF)</th>
            <th class="px-6 py-4">Net Salary (RWF)</th>
            <th class="px-6 py-4">Status</th>
            <th class="px-6 py-4">Action</th>
          </tr>
        </thead>
        <tbody>
          {#each payslips as p}
            <tr class="border-b border-gray-200 hover:bg-gray-50">
              <td class="px-6 py-4 font-medium">{p.month}/{p.year}</td>
              <td class="px-6 py-4 font-mono">{formatCurrency(p.gross)}</td>
              <td class="px-6 py-4 font-mono font-semibold text-green-700">{formatCurrency(p.netSalary)}</td>
              <td class="px-6 py-4">
                <span class="px-2 py-1 bg-green-100 text-green-800 rounded-md text-xs">{p.status || 'Paid'}</span>
              </td>
              <td class="px-6 py-4">
                <Button onclick={() => openDetails(p)} class="!py-1 !px-3 text-xs bg-gray-100 !text-gray-800 hover:bg-gray-200">View Breakdown</Button>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</Card>

{#if selectedPayslip}
  <div class="fixed inset-0 bg-black/50 bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-md shadow-2xl max-w-2xl w-full flex flex-col max-h-[90vh]">
      
      <!-- Modal Header -->
      <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center bg-gray-50 rounded-t-md">
        <div>
          <h3 class="text-xl font-bold text-gray-900">Payslip Breakdown</h3>
          <p class="text-sm text-gray-500">Period: {selectedPayslip.month}/{selectedPayslip.year} &nbsp;|&nbsp; Status: <span class="font-semibold text-green-600">{selectedPayslip.status || 'Paid'}</span></p>
        </div>
        <button onclick={closeDetails} class="text-gray-400 hover:text-gray-600 text-2xl font-bold">&times;</button>
      </div>

      <!-- Modal Body (Scrollable) -->
      <div class="p-6 overflow-y-auto">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          
          <!-- Earnings Table -->
          <div>
            <h4 class="text-sm font-bold uppercase tracking-wider text-gray-500 mb-3 border-b pb-2">Earnings & Allowances</h4>
            <table class="w-full text-sm">
              <tbody>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">Base Salary</td>
                  <td class="py-2 text-right font-mono">{formatCurrency(selectedPayslip.base)}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">Housing Allowance</td>
                  <td class="py-2 text-right font-mono">{formatCurrency(selectedPayslip.house)}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">Transport Allowance</td>
                  <td class="py-2 text-right font-mono">{formatCurrency(selectedPayslip.transport)}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr class="bg-gray-50 border-t border-gray-200">
                  <td class="py-3 px-2">
                    <span class="font-bold text-gray-900 block">Gross Earnings</span>
                    <span class="text-xs text-gray-500 font-mono">({formatCurrency(selectedPayslip.base)} + {formatCurrency(selectedPayslip.house)} + {formatCurrency(selectedPayslip.transport)})</span>
                  </td>
                  <td class="py-3 px-2 text-right font-mono font-bold text-gray-900 align-top">{formatCurrency(selectedPayslip.gross)}</td>
                </tr>
              </tfoot>
            </table>
          </div>

          <!-- Deductions Table -->
          <div>
            <h4 class="text-sm font-bold uppercase tracking-wider text-gray-500 mb-3 border-b pb-2">Taxes & Deductions</h4>
            <table class="w-full text-sm">
              <tbody>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">PAYE (Income Tax)</td>
                  <td class="py-2 text-right font-mono text-red-600">-{formatCurrency(selectedPayslip.tax)}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">CSR (Pension)</td>
                  <td class="py-2 text-right font-mono text-red-600">-{formatCurrency(selectedPayslip.pension)}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">RAMA (Medical)</td>
                  <td class="py-2 text-right font-mono text-red-600">-{formatCurrency(selectedPayslip.medical)}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-2 text-gray-700">Other Deductions</td>
                  <td class="py-2 text-right font-mono text-red-600">-{formatCurrency(selectedPayslip.other)}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr class="bg-gray-50 border-t border-gray-200">
                  <td class="py-3 px-2">
                    <span class="font-bold text-gray-900 block">Total Deductions</span>
                    <span class="text-xs text-gray-500 font-mono">({formatCurrency(selectedPayslip.tax)} + {formatCurrency(selectedPayslip.pension)} + {formatCurrency(selectedPayslip.medical)} + {formatCurrency(selectedPayslip.other)})</span>
                  </td>
                  <td class="py-3 px-2 text-right font-mono font-bold text-red-700 align-top">-{formatCurrency((selectedPayslip.tax || 0) + (selectedPayslip.pension || 0) + (selectedPayslip.medical || 0) + (selectedPayslip.other || 0))}</td>
                </tr>
              </tfoot>
            </table>
          </div>

        </div>

        <!-- Net Salary Summary Footer -->
        <div class="mt-8 bg-rwanda-blue text-white rounded-lg p-6 shadow-inner flex justify-between items-center">
          <div>
            <h4 class="text-lg font-medium opacity-90">Net Take-Home Pay</h4>
            <p class="text-sm font-mono opacity-80 mt-1 bg-white/10 px-2 py-1 rounded inline-block">Formula: {formatCurrency(selectedPayslip.gross)} - {formatCurrency((selectedPayslip.tax || 0) + (selectedPayslip.pension || 0) + (selectedPayslip.medical || 0) + (selectedPayslip.other || 0))}</p>
          </div>
          <div class="text-3xl font-bold font-mono tracking-tight">
            {formatCurrency(selectedPayslip.netSalary)} <span class="text-lg font-normal opacity-75">RWF</span>
          </div>
        </div>
      </div>

      <!-- Modal Footer -->
      <div class="px-6 py-4 border-t border-gray-200 flex justify-end bg-gray-50 rounded-b-md">
        <Button onclick={closeDetails} class="!bg-white !text-gray-700 border border-gray-300 hover:!bg-gray-100">Close</Button>
      </div>

    </div>
  </div>
{/if}
