/*
 * MIT License
 *
 * Copyright (c) 2023 Artiom Bozieac
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import * as React from 'react';
import Grid from '@mui/material/Grid';
import Container from '@mui/material/Container';

import Copyright from '../../footer/Copyright';
import Header from '../../Header';
import ProductCard from './ProductCard';
import { getProducts } from '../../../api/ProductRequest';
import Product from './Product';

import { useAppSelector } from '../../../hooks';
import { Box, Paper, Typography, Pagination } from '@mui/material';
import Banner from "../../../../img/banner.jpg";
import { getTranslation } from '../../../../i18n/i18n';
import { PaginatedDTO } from '../../../../types/PaginatedDTO';

const ProductsContainer = () => {
  const lang = useAppSelector(state => state.lang.lang);

  const [page, setPage] = React.useState(0);

  const [pagination, setPagination] = React.useState<PaginatedDTO<Product>>({
    data: [],
    currentPage: 0,
    totalPages: 0,
    totalItems: 0,
    totalPrice: 0,
    pageSize: 10,
    hasNext: false,
    hasPrevious: false,
  });

  React.useEffect(() => {
    const fetchProducts = async () => {
      const res = await getProducts(page, 8);

      setPagination(res);
    };

    fetchProducts();
  }, [page]);

  return (
    <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <Header />
      <Container maxWidth="xl" sx={{ flex: 1, py: 4 }}>

        {/* Banner */}
        <Box
          sx={{
            position: 'relative',
            width: '100%',
            height: { xs: '200px', sm: '300px', md: '400px' },
            mb: 4,
            borderRadius: 2,
            overflow: 'hidden',
            boxShadow: 3,
          }}
        >
          <img
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            alt="banner"
            src={Banner}
          />
          <Box
            sx={{
              position: 'absolute',
              inset: 0,
              background: 'linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.2))',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <Typography
              variant="h2"
              color="white"
              sx={{ textAlign: 'center', fontWeight: 700, textShadow: '2px 2px 4px rgba(0,0,0,0.5)' }}
            >
              {getTranslation(lang, "ecommerce_platform")}
            </Typography>
          </Box>
        </Box>

        {/* Products */}
        <Paper elevation={0} sx={{ p: 3, borderRadius: 2, bgcolor: 'background.default' }}>
          {pagination.data.length > 0 ? (
            <Grid container spacing={3} justifyContent="center">
              {pagination.data.map(product => (
                <Grid item key={product.id}>
                  <ProductCard
                    id={product.id}
                    title={product.name}
                    price={product.price}
                    categories={product.categories}
                    description={product.description}
                  />
                </Grid>
              ))}
            </Grid>
          ) : (
            <Box sx={{ py: 8, textAlign: 'center' }}>
              <Typography variant="h4" color="text.secondary" sx={{ mb: 2, fontWeight: 500 }}>
                {getTranslation(lang, "no_products_found")}
              </Typography>
            </Box>
          )}
        </Paper>

        {/* Pagination */}
        <Box display="flex" justifyContent="center" mt={2}>
          <Pagination
            count={pagination.totalPages || 1}
            page={page + 1}
            onChange={(_, value) => setPage(value - 1)}
          />
        </Box>

      </Container>
      <Copyright />
    </Box>
  );
};

export default ProductsContainer;