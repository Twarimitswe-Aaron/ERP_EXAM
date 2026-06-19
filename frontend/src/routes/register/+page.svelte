<script lang="ts">
  import { slide } from 'svelte/transition';
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
  
  let passwordLength = $derived(password.length >= 5);
  let passwordUpper = $derived(/[A-Z]/.test(password));
  let passwordDigit = $derived(/[0-9]/.test(password));
  let passwordSpecial = $derived(/[!@#$%^&*(),.?":{}|<>]/.test(password));

  let passwordValid = $derived(password.length === 0 || (passwordLength && passwordUpper && passwordDigit && passwordSpecial));

  let isFormValid = $derived(
    email.length > 0 && password.length > 0 && !emailError && passwordValid
  );

  async function handleRegister() {
    if (!isFormValid) return;
    
    error = '';
    loading = true;
    try {
      const response = await api.post('/auth/register', { email, password });
      if (response.token) {
        setAuthToken(response.token);
        goto('/dashboard');
      } else {
        error = 'Registration failed. Invalid response from server.';
      }
    } catch (err: any) {
      error = err.message || 'Registration failed. Email might already be in use.';
    } finally {
      loading = false;
    }
  }
</script>

<div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
  <div class="max-w-md w-full">
    <div class="text-center mb-8">
      <h2 class="text-3xl font-extrabold text-gray-900">Create an Account</h2>
      <p class="mt-2 text-sm text-gray-600">Join the Government ERP System</p>
    </div>

    <Card>
      {#if error}
        <div class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md mb-4 text-sm">
          {error}
        </div>
      {/if}

      <form onsubmit={(e) => { e.preventDefault(); handleRegister(); }}>
        <Input id="email" type="email" label="Email Address" bind:value={email} error={emailError} required />
        <Input id="password" type="password" label="Password" bind:value={password} required />
        
        {#if password.length > 0 && !passwordValid}
          <div class="mt-2 text-sm bg-gray-50 p-3 rounded-md border border-gray-200" transition:slide={{ duration: 200 }}>
            <p class="text-gray-700 font-medium mb-1">Password must contain:</p>
            <ul class="list-disc pl-5 space-y-1">
              {#if !passwordLength}
                <li class="text-red-500 transition-colors">At least 5 characters</li>
              {/if}
              {#if !passwordUpper}
                <li class="text-red-500 transition-colors">One uppercase letter</li>
              {/if}
              {#if !passwordDigit}
                <li class="text-red-500 transition-colors">One digit (0-9)</li>
              {/if}
              {#if !passwordSpecial}
                <li class="text-red-500 transition-colors">One special character</li>
              {/if}
            </ul>
          </div>
        {/if}

        <div class="mt-6">
          <Button type="submit" class="w-full" disabled={!isFormValid || loading}>
            {loading ? 'Creating account...' : 'Register'}
          </Button>
        </div>
      </form>
      
      <div class="mt-4 text-center">
        <p class="text-sm text-gray-600">
          Already have an account? 
          <a href="/login" class="font-medium text-rwanda-blue hover:text-blue-600">Sign in here</a>
        </p>
      </div>
    </Card>
  </div>
</div>
