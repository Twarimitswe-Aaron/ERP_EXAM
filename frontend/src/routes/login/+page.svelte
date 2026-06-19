<script lang="ts">
  import { goto } from '$app/navigation';
  import { api, setAuthToken } from '$lib/api';
  import Card from '$lib/components/Card.svelte';
  import Input from '$lib/components/Input.svelte';
  import Button from '$lib/components/Button.svelte';

  let email = $state('');
  let password = $state('');
  let error = $state('');
  let loading = $state(false);

  // Real-time validation
  let emailError = $derived(
    email.length > 0 && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) 
      ? 'Please enter a valid email address' 
      : ''
  );
  
  let passwordError = $derived(
    password.length > 0 && password.length < 5 
      ? 'Password must be at least 5 characters' 
      : ''
  );

  let isFormValid = $derived(
    email.length > 0 && password.length > 0 && !emailError && !passwordError
  );

  async function handleLogin() {
    if (!isFormValid) return;
    
    error = '';
    loading = true;
    try {
      const response = await api.post('/auth/login', { email, password });
      if (response.token) {
        setAuthToken(response.token);
        goto('/dashboard');
      } else {
        error = 'Login failed. Invalid response from server.';
      }
    } catch (err: any) {
      error = err.message || 'Login failed. Please check your credentials.';
    } finally {
      loading = false;
    }
  }
</script>

<div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
  <div class="max-w-md w-full">
    <div class="text-center mb-8">
      <h2 class="text-3xl font-extrabold text-gray-900">Government ERP</h2>
      <p class="mt-2 text-sm text-gray-600">Payroll Management System</p>
    </div>

    <Card>
      {#if error}
        <div class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md mb-4 text-sm">
          {error}
        </div>
      {/if}

      <form onsubmit={(e) => { e.preventDefault(); handleLogin(); }}>
        <Input id="email" type="email" label="Email Address" bind:value={email} error={emailError} required />
        <Input id="password" type="password" label="Password" bind:value={password} error={passwordError} required />
        
        <div class="mt-6">
          <Button type="submit" class="w-full" disabled={!isFormValid || loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </Button>
        </div>
      </form>
      
      <div class="mt-4 text-center">
        <p class="text-sm text-gray-600">
          Don't have an account yet? 
          <a href="/register" class="font-medium text-rwanda-blue hover:text-blue-600">Register here</a>
        </p>
      </div>
    </Card>
  </div>
</div>
