import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import LocalMallIcon from '@mui/icons-material/LocalMall';
import { useAppDispatch, useAppSelector } from '../hooks';
import { getUserPhoto } from '../api/UserRequest';
import { updateJwt } from '../slices/jwtSlice';
import { Select, InputLabel, SelectChangeEvent, Button, Divider } from '@mui/material';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import HomeIcon from '@mui/icons-material/Home';
import SearchIcon from '@mui/icons-material/Search';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import AddIcon from '@mui/icons-material/Add';
import SellIcon from '@mui/icons-material/Sell';
import ReceiptIcon from '@mui/icons-material/Receipt';

import { useNavigate } from 'react-router-dom';
import Lang from '../../i18n/LangEnum';
import { getTranslation } from '../../i18n/i18n';
import { updateLang } from '../slices/langSlice';
import { setCookie } from '../utils/Utils';
import { updateTheme } from '../slices/themeSlice';
import MenuIcon from '@mui/icons-material/Menu';
import LogoutIcon from '@mui/icons-material/Logout';

const Header = () => {
    const roles = useAppSelector(state => state.info.info.roles);
    const lang = useAppSelector(state => state.lang.lang);
    const theme = useAppSelector(state => state.theme.theme);
    const username = useAppSelector(state => state.username.sub);
    const dispatch = useAppDispatch();

    const [photo, setPhoto] = React.useState("");

    const navigate = useNavigate();
    const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);
    const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);

    React.useEffect(() => {
        const getUserPhotoRequest = async () => {
            const photoBlob = await getUserPhoto(username);

            if ((photoBlob as Response).status) {
                if ((photoBlob as Response).status == 401) {
                    navigate("/login");
                }
            }

            setPhoto(URL.createObjectURL(photoBlob as Blob));
        }

        getUserPhotoRequest();
    }, []);

    const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    const handleHome = () => {
        navigate("/products");
    }

    const handleMyCart = () => {
        navigate("/cart");
    }

    const handleAddProduct = () => {
        navigate("/product/add");
    }

    const handleProfile = () => {
        navigate("/profile");
    }

    const handleSearch = () => {
        navigate("/search");
    }

    const handleMyProducts = () => {
        navigate("/profile/products");
    }

    const handleMyOrders = () => {
        navigate("/orders");
    }

    const handleLogout = () => {
        dispatch(updateJwt(""));
        navigate("/login");
    }

    const handleLanguageSelection = (e: SelectChangeEvent<string>) => {
        setCookie("ecommerce-platform-lang", e.target.value);

        Object.keys(Lang).forEach(lang => {
            if (e.target.value.localeCompare(lang) == 0) {
                dispatch(updateLang(lang as Lang));
            }
        });
    }

    const handleChangeTheme = (e: SelectChangeEvent<string>) => {
        const isLight = e.target.value.localeCompare(getTranslation(lang, "light_theme")) == 0 ? "true" : "false";

        setCookie("ecommerce-platform-light-theme", isLight);
        dispatch(updateTheme(isLight == "true"));
    }

    return (
        <AppBar
            position="sticky"
            elevation={0}
            sx={{
                backdropFilter: 'blur(6px)',
                backgroundColor: theme ? 'rgba(255, 255, 255, 0.6)' : 'rgba(18, 18, 18, 0.6)',
                borderBottom: '1px solid',
                borderColor: 'divider',
                color: 'text.primary',
            }}
        >
            <Container maxWidth="lg" data-testid="header">
                <Toolbar
                    disableGutters
                    sx={{
                        minHeight: { xs: 64, md: 72 },
                        py: 1,
                    }}
                >
                    {/* Mobile Menu */}
                    <Box sx={{ display: { xs: 'flex', md: 'none' }, alignItems: 'center' }}>
                        <IconButton
                            size="large"
                            aria-label="menu"
                            onClick={handleOpenNavMenu}
                            color="inherit"
                            sx={{ mr: 1 }}
                        >
                            <MenuIcon />
                        </IconButton>
                        <Menu
                            id="mobile-menu"
                            anchorEl={anchorElNav}
                            open={Boolean(anchorElNav)}
                            onClose={handleCloseNavMenu}
                            PaperProps={{
                                elevation: 0,
                                sx: {
                                    mt: 1.5,
                                    borderRadius: 2,
                                    border: 1,
                                    borderColor: 'divider',
                                    minWidth: 200,
                                }
                            }}
                        >
                            {roles[0].name === "USER" && (
                                <>
                                    <MenuItem onClick={handleMyOrders}>
                                        <ReceiptIcon sx={{ mr: 1.5 }} />
                                        <Typography>{getTranslation(lang, "my_orders") || "My Orders"}</Typography>
                                    </MenuItem>
                                    <MenuItem onClick={handleMyCart}>
                                        <ShoppingCartIcon sx={{ mr: 1.5 }} />
                                        <Typography>{getTranslation(lang, "my_cart")}</Typography>
                                    </MenuItem>
                                </>
                            )}
                            {roles[0].name === "SELLER" && (
                                <>
                                    <MenuItem onClick={handleAddProduct}>
                                        <AddIcon sx={{ mr: 1.5 }} />
                                        <Typography>{getTranslation(lang, "add_product")}</Typography>
                                    </MenuItem>
                                    <MenuItem onClick={handleMyProducts}>
                                        <SellIcon sx={{ mr: 1.5 }} />
                                        <Typography>{getTranslation(lang, "my_products")}</Typography>
                                    </MenuItem>
                                </>
                            )}
                            {roles[0].name === "USER" && (
                                <MenuItem onClick={handleSearch}>
                                    <SearchIcon sx={{ mr: 1.5 }} />
                                    <Typography>{getTranslation(lang, "search")}</Typography>
                                </MenuItem>
                            )}
                        </Menu>
                    </Box>

                    {/* Logo */}
                    <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: { xs: 1, md: 0 } }}>
                        <LocalMallIcon
                            sx={{
                                display: 'flex',
                                mr: 1,
                                color: 'primary.main',
                                fontSize: { xs: 28, md: 32 },
                            }}
                        />
                        <Typography
                            id="home-title"
                            variant="h5"
                            component="a"
                            href="#/products"
                            sx={{
                                fontWeight: 700,
                                fontSize: { xs: 20, md: 24 },
                                letterSpacing: '.1rem',
                                color: 'inherit',
                                textDecoration: 'none',
                                '&:hover': {
                                    color: 'primary.main',
                                },
                            }}
                        >
                            {getTranslation(lang, "ecommerce_platform")}
                        </Typography>
                    </Box>

                    {/* Desktop Navigation */}
                    <Box
                        sx={{
                            flexGrow: 1,
                            display: { xs: 'none', md: 'flex' },
                            justifyContent: 'center',
                            gap: 2,
                        }}
                    >
                        <Button
                            startIcon={<HomeIcon />}
                            onClick={handleHome}
                            id="home-button"
                            sx={{
                                color: 'text.primary',
                                borderRadius: 2,
                                px: 2,
                                transition: 'all 0.2s',
                                '&:hover': {
                                    bgcolor: 'primary.main',
                                    color: 'white',
                                },
                            }}
                        >
                            {getTranslation(lang, "home")}
                        </Button>
                        <Button
                            startIcon={<AccountCircleIcon />}
                            onClick={handleProfile}
                            id="profile-button"
                            sx={{
                                color: 'text.primary',
                                borderRadius: 2,
                                px: 2,
                                transition: 'all 0.2s',
                                '&:hover': {
                                    bgcolor: 'primary.main',
                                    color: 'white',
                                },
                            }}
                        >
                            {getTranslation(lang, "profile")}
                        </Button>

                        {roles[0].name === "USER" && (
                            <>
                                <Button
                                    startIcon={<ShoppingCartIcon />}
                                    onClick={handleMyCart}
                                    id="my-cart-button"
                                    sx={{
                                        color: 'text.primary',
                                        borderRadius: 2,
                                        px: 2,
                                        transition: 'all 0.2s',
                                        '&:hover': {
                                            bgcolor: 'primary.main',
                                            color: 'white',
                                        },
                                    }}
                                >
                                    {getTranslation(lang, "my_cart")}
                                </Button>

                                <Button
                                    startIcon={<ReceiptIcon />}
                                    onClick={handleMyOrders}
                                    id="my-orders-button"
                                    sx={{
                                        color: 'text.primary',
                                        borderRadius: 2,
                                        px: 2,
                                        transition: 'all 0.2s',
                                        '&:hover': {
                                            bgcolor: 'primary.main',
                                            color: 'white',
                                        },
                                    }}
                                >
                                    {getTranslation(lang, "my_orders") || "My Orders"}
                                </Button>
                            </>
                        )}

                        {roles[0].name === "SELLER" && (
                            <>
                                <Button
                                    startIcon={<AddIcon />}
                                    onClick={handleAddProduct}
                                    id="add-product-button"
                                    sx={{
                                        color: 'text.primary',
                                        borderRadius: 2,
                                        px: 2,
                                        transition: 'all 0.2s',
                                        '&:hover': {
                                            bgcolor: 'primary.main',
                                            color: 'white',
                                        },
                                    }}
                                >
                                    {getTranslation(lang, "add_product")}
                                </Button>
                                <Button
                                    startIcon={<SellIcon />}
                                    onClick={handleMyProducts}
                                    id="my-products-button"
                                    sx={{
                                        color: 'text.primary',
                                        borderRadius: 2,
                                        px: 2,
                                        transition: 'all 0.2s',
                                        '&:hover': {
                                            bgcolor: 'primary.main',
                                            color: 'white',
                                        },
                                    }}
                                >
                                    {getTranslation(lang, "my_products")}
                                </Button>
                            </>
                        )}

                        {roles[0].name === "USER" && (
                            <Button
                                startIcon={<SearchIcon />}
                                onClick={handleSearch}
                                sx={{
                                    color: 'text.primary',
                                    borderRadius: 2,
                                    px: 2,
                                    transition: 'all 0.2s',
                                    '&:hover': {
                                        bgcolor: 'primary.main',
                                        color: 'white',
                                    },
                                }}
                            >
                                {getTranslation(lang, "search")}
                            </Button>
                        )}
                    </Box>

                    {/* User Menu */}
                    <Box sx={{ flexShrink: 0 }}>
                        <Tooltip title={getTranslation(lang, "open_settings")}>
                            <IconButton
                                onClick={handleOpenUserMenu}
                                sx={{
                                    p: 0.5,
                                    border: 2,
                                    borderColor: 'transparent',
                                    '&:hover': {
                                        borderColor: 'primary.main',
                                    },
                                }}
                            >
                                <Avatar
                                    src={photo}
                                    sx={{
                                        width: 40,
                                        height: 40,
                                        bgcolor: 'primary.main',
                                    }}
                                />
                            </IconButton>
                        </Tooltip>
                        <Menu
                            id="user-menu"
                            anchorEl={anchorElUser}
                            open={Boolean(anchorElUser)}
                            onClose={handleCloseUserMenu}
                            PaperProps={{
                                elevation: 0,
                                sx: {
                                    mt: 1.5,
                                    borderRadius: 2,
                                    border: 1,
                                    borderColor: 'divider',
                                    minWidth: 200,
                                }
                            }}
                        >
                            <MenuItem onClick={handleProfile}>
                                <AccountCircleIcon sx={{ mr: 1.5 }} />
                                <Typography>{getTranslation(lang, "profile")}</Typography>
                            </MenuItem>
                            <Divider />
                            <Box sx={{ px: 2, py: 1 }}>
                                <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1 }}>
                                    {getTranslation(lang, "language")}
                                </Typography>
                                <Select
                                    fullWidth
                                    size="small"
                                    value={lang.toString()}
                                    onChange={(value) => handleLanguageSelection(value)}
                                    sx={{ mb: 2 }}
                                >
                                    <MenuItem value={Lang.ENG.toString()}>{getTranslation(lang, "english")}</MenuItem>
                                    <MenuItem value={Lang.RU.toString()}>{getTranslation(lang, "russian")}</MenuItem>
                                    <MenuItem value={Lang.RO.toString()}>{getTranslation(lang, "romanian")}</MenuItem>
                                </Select>

                                <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1 }}>
                                    {getTranslation(lang, "theme")}
                                </Typography>
                                <Select
                                    fullWidth
                                    size="small"
                                    value={theme ? getTranslation(lang, "light_theme") : getTranslation(lang, "dark_theme")}
                                    onChange={(value) => handleChangeTheme(value)}
                                >
                                    <MenuItem value={getTranslation(lang, "dark_theme")}>{getTranslation(lang, "dark_theme")}</MenuItem>
                                    <MenuItem value={getTranslation(lang, "light_theme")}>{getTranslation(lang, "light_theme")}</MenuItem>
                                </Select>
                            </Box>
                            <Divider />
                            <MenuItem onClick={handleLogout} sx={{ color: 'error.main' }}>
                                <LogoutIcon sx={{ mr: 1.5 }} />
                                <Typography>{getTranslation(lang, "logout")}</Typography>
                            </MenuItem>
                        </Menu>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}
export default Header;