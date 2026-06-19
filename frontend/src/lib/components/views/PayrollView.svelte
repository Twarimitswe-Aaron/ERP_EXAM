<script lang="ts">
  import { api } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Button from '$lib/components/Button.svelte';
  import Input from '$lib/components/Input.svelte';

  let month = $state(new Date().getMonth() + 1);
  let year = $state(new Date().getFullYear());
  let loading = $state(false);
  let message = $state('');
  
  async function handleGenerate() {
    loading = true;
    message = '';
    try {
      const response = await api.post('/payroll/generate', { month, year });
      message = 'Payroll generated successfully!';
    } catch (err: any) {
      message = err.message || 'Failed to generate payroll.';
    } finally {
      loading = false;
    }
  }
</script>

<Card title="Generate Payroll">
  <form onsubmit={(e) => { e.preventDefault(); handleGenerate(); }} class="max-w-md">
    <div class="grid grid-cols-2 gap-4">
      <Input id="month" type="number" label="Month (1-12)" bind:value={month} required />
      <Input id="year" type="number" label="Year" bind:value={year} required />
    </div>
    
    <Button type="submit" class="mt-4" disabled={loading}>
      {loading ? 'Processing...' : 'Generate Payroll'}
    </Button>
    
    {#if message}
      <p class="mt-4 text-sm font-medium p-3 rounded-md bg-gray-100">{message}</p>
    {/if}
  </form>
</Card>
