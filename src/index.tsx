/* eslint-disable @typescript-eslint/no-unused-vars */
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';

const stripePromise = loadStripe('pk_test_51QiKdoCIQ3QGxpKn0UMbkZHNnrRPFZlbLm6ceATeLRB7EFCoDt70BaJFBsXhikdjbpd7iUSZ7D38W9hH25aAKC4S00rdEQh2up');


const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <BrowserRouter>
  <Elements stripe={stripePromise}>
     <App />
  </Elements>
  </BrowserRouter>
);

