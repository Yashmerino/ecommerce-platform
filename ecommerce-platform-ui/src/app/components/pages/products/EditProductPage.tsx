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
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import Button from '@mui/material/Button';
import { getProductPhoto, setProductPhoto, updateProduct } from '../../../api/ProductRequest';
import Alert from '@mui/material/Alert';
import OutlinedInput from '@mui/material/OutlinedInput';
import ListItemText from '@mui/material/ListItemText';
import Select, { SelectChangeEvent } from '@mui/material/Select';
import Checkbox from '@mui/material/Checkbox';
import { InputError, isFieldPresentInInputErrors } from '../../../utils/InputErrorUtils';
import InputFields from '../../../utils/InputFields';
import { Paper, Snackbar, Typography } from '@mui/material';
import { useLocation, useNavigate } from "react-router-dom";
import { Category } from './AddProductPage';

import Header from '../../Header';
import Copyright from '../../footer/Copyright';

import { useAppSelector } from '../../../hooks'
import { getCategories } from '../../../api/CategoryRequest';
import { getTranslation } from '../../../../i18n/i18n';
import NoPhoto from "../../../../img/no_photo.jpg";

const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: 48 * 4.5 + 8,
            width: 350,
        },
    },
};

const EditProductPage = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const lang = useAppSelector(state => state.lang.lang);
    const roles = useAppSelector(state => state.info.info.roles);
    const jwt = useAppSelector(state => state.jwt);

    const [name, setName] = React.useState(location.state ? location.state.title : "default");
    const [description, setDescription] = React.useState(location.state ? location.state.description : "default");
    const [price, setPrice] = React.useState(location.state ? location.state.price : 0.01);
    const [categories, setCategories] = React.useState<string[]>(location.state ? location.state.categories ? location.state.categories.map((category: Category) => category.name) : [] : []);
    const [fetchedCategories, setFetchedCategories] = React.useState<Category[]>([]);

    const [inputErrors, setInputErrors] = React.useState<InputError[]>([]);
    const [error, setError] = React.useState("");
    const [isSuccess, setSuccess] = React.useState(false);

    const [photo, setPhoto] = React.useState(NoPhoto);
    const [file, setFile] = React.useState<File | null>(null);

    const handleAlertClick = () => {
        setSuccess(false);
        setError("");
    };

    React.useEffect(() => {
        const token = jwt.token;

        const fetchCategories = async () => {
            const categoriesRequest = await getCategories(token);

            if (categoriesRequest.status) {
                if (categoriesRequest.status == 401) {
                    navigate("/login");
                }
            }

            setFetchedCategories(categoriesRequest);
        }

        fetchCategories(); // NOSONAR: It should not await.

        const getProductPhotoRequest = async () => {
            const photoBlob = await getProductPhoto(location.state ? location.state.id : "1");
            if(photoBlob.size > 0) {
                setPhoto(URL.createObjectURL(photoBlob));
                setFile(photoBlob as File);
            }

        }

        getProductPhotoRequest();
    }, []);

    const handleSubmit = async () => {
        setSuccess(false);
        setInputErrors([]);
        setError("");

        let categoriesDTO: Category[] = [];
        categories.forEach((category) => {
            fetchedCategories.forEach((fetchedCategory) => {
                if (fetchedCategory.name.localeCompare(category) == 0) {
                    const categoryDTO = {
                        id: fetchedCategory.id,
                        name: fetchedCategory.name
                    }

                    categoriesDTO.push(categoryDTO);
                }
            }
            )
        })

        const response = await updateProduct(jwt.token, location.state ? location.state.id : 1, name, categoriesDTO, price, description);

        if (response.error) {
            setError(response.error);
        } else if (response.fieldErrors) {
            setInputErrors(response.fieldErrors);
        } else {
            if (file != null) {
                await setProductPhoto(jwt.token, location.state ? location.state.id : 1, file);
            }

            setSuccess(true);
            navigate("/profile/products");
        }
    }

    const handleCategoriesChange = (event: SelectChangeEvent<typeof categories>) => {
        const { target: { value }, } = event;
        setCategories(typeof value === 'string' ? value.split(',') : value);
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setFile(e.target.files[0]);

            let reader = new FileReader();
            reader.readAsDataURL(e.target.files[0] as Blob);
            reader.onload = function () {
                setPhoto(reader.result as string);
            };
        }
    };

    return (
        <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
            <Header />
            {isSuccess && (
                <Snackbar 
                    open={isSuccess} 
                    autoHideDuration={2000} 
                    onClose={handleAlertClick}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                >
                    <Alert data-testid="alert-success" id="alert-success" onClose={handleAlertClick} severity="success" sx={{ width: '100%' }}>
                        {getTranslation(lang, "product_updated_successfully")}
                    </Alert>
                </Snackbar>
            )}
            {error.length > 0 && (
                <Snackbar 
                    open={error.length > 0} 
                    autoHideDuration={2000} 
                    onClose={handleAlertClick}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                >
                    <Alert data-testid="alert-error" id="alert-error" onClose={handleAlertClick} severity="error" sx={{ width: '100%' }}>
                        {getTranslation(lang, error)}
                    </Alert>
                </Snackbar>
            )}
            <Container maxWidth="lg" sx={{ flex: 1, py: 4 }}>
                <Box sx={{ mb: 4 }}>
                    <Typography
                        variant="h4"
                        fontWeight={700}
                        sx={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: 2,
                            color: 'primary.main'
                        }}
                    >
                        {getTranslation(lang, "update_product")}
                    </Typography>
                </Box>

                {// @ts-ignore 
                    roles[0].name === "SELLER" ? (
                        <Paper 
                            elevation={2} 
                            sx={{ 
                                borderRadius: 2,
                                overflow: 'hidden',
                                bgcolor: 'background.paper',
                                p: 4,
                                display: 'flex',
                                flexDirection: { xs: 'column', md: 'row' },
                                gap: 4
                            }}
                        >
                            <Box sx={{
                                width: { xs: '100%', md: '300px' },
                                alignSelf: 'start'
                            }}>
                                <input
                                    accept="image/*"
                                    style={{ display: 'none' }}
                                    id="photo-upload-button"
                                    type="file"
                                    onChange={handleFileChange}
                                />
                                <label htmlFor="photo-upload-button">
                                    <Button 
                                        component="span" 
                                        sx={{ 
                                            width: '100%',
                                            aspectRatio: '1/1',
                                            p: 0,
                                            overflow: 'hidden',
                                            borderRadius: 2,
                                            boxShadow: 2,
                                            '&:hover': {
                                                boxShadow: 4,
                                                '& img': {
                                                    transform: 'scale(1.05)'
                                                }
                                            }
                                        }}
                                    >
                                        <img 
                                            width="100%" 
                                            height="100%" 
                                            className="user-image" 
                                            src={photo}
                                            alt="product-photo" 
                                            data-testid="photo"
                                            style={{
                                                objectFit: 'cover',
                                                transition: 'transform 0.3s ease-in-out'
                                            }}
                                        />
                                    </Button>
                                </label>
                            </Box>

                            <Box sx={{ flex: 1 }}>
                                <Box sx={{
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: 2
                                }}>
                                    <TextField
                                        defaultValue={name}
                                        error={isFieldPresentInInputErrors(InputFields.NAME, inputErrors)}
                                        required
                                        fullWidth
                                        name="name"
                                        label={getTranslation(lang, "name")}
                                        type="name"
                                        id="name-field"
                                        data-testid="name-field"
                                        autoComplete="current-name"
                                        helperText={inputErrors.find(error => error.field === InputFields.NAME)?.message ? getTranslation(lang, inputErrors.find(error => error.field === InputFields.NAME)?.message || '') : undefined}
                                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                                            setName(event.target.value);
                                        }}
                                    />
                                    <TextField
                                        defaultValue={description}
                                        fullWidth
                                        multiline
                                        rows={4}
                                        error={isFieldPresentInInputErrors(InputFields.DESCRIPTION, inputErrors)}
                                        name="description"
                                        label={getTranslation(lang, "description")}
                                        type="description"
                                        id="description"
                                        data-testid="description"
                                        autoComplete="current-description"
                                        helperText={inputErrors.find(error => error.field === InputFields.DESCRIPTION)?.message ? getTranslation(lang, inputErrors.find(error => error.field === InputFields.DESCRIPTION)?.message || '') : undefined}
                                        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                                            setDescription(event.target.value);
                                        }}
                                    />
                                    <Box sx={{ display: 'flex', gap: 2 }}>
                                        <TextField
                                            value={price}
                                            error={isFieldPresentInInputErrors(InputFields.PRICE, inputErrors)}
                                            onChange={(event) => { setPrice(Number(event.target.value)) }}
                                            required
                                            id="price-field"
                                            data-testid="price-field"
                                            label={getTranslation(lang, "price") + '€'}
                                            type="number"
                                            inputProps={{ min: 0.01 }}
                                            helperText={inputErrors.find(error => error.field === InputFields.PRICE)?.message ? getTranslation(lang, inputErrors.find(error => error.field === InputFields.PRICE)?.message || '') : undefined}
                                            sx={{ flex: 1 }}
                                        />
                                        <Select
                                            id="categories-field"
                                            data-testid="categories-field"
                                            displayEmpty
                                            multiple
                                            label={getTranslation(lang, "categories")}
                                            value={categories}
                                            onChange={handleCategoriesChange}
                                            input={<OutlinedInput />}
                                            renderValue={(selected) => (
                                                selected.length === 0
                                                    ? (<em>{getTranslation(lang, "categories")}</em>)
                                                    : (<em>{getTranslation(lang, "categories")}</em>)
                                            )}
                                            MenuProps={MenuProps}
                                            sx={{ flex: 2, height: '100%' }}
                                        >
                                            {fetchedCategories.map((fetchedCategory) => (
                                                <MenuItem key={fetchedCategory.name} value={fetchedCategory.name}>
                                                    <Checkbox checked={categories.indexOf(fetchedCategory.name) > -1} />
                                                    <ListItemText primary={getTranslation(lang, fetchedCategory.name)} />
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </Box>
                                </Box>
                                <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
                                    <Button
                                        type="submit"
                                        data-testid="submit-button"
                                        variant="contained"
                                        onClick={handleSubmit}
                                        sx={{
                                            px: 4,
                                            py: 1,
                                            borderRadius: 2,
                                        }}
                                    >
                                        {getTranslation(lang, "update")}
                                    </Button>
                                </Box>
                            </Box>
                        </Paper>
                    ) : (
                        <Typography 
                            variant="h6" 
                            color="text.secondary" 
                            align='center' 
                            py={8}
                        >
                            {getTranslation(lang, "no_rights_to_access")}
                        </Typography>
                    )}
            </Container>
            <Copyright />
        </Box>
    );
}

export default EditProductPage;