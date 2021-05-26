import React from 'react';
import { BrowserRouter, Link } from 'react-router-dom';
import { withTheme } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import Backdrop from '@material-ui/core/Backdrop';
import Drawer from '@material-ui/core/Drawer';
import Box from '@material-ui/core/Box';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Tooltip from '@material-ui/core/Tooltip';
import List from '@material-ui/core/List';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import SendIcon from '@material-ui/icons/Send';
import IconButton from '@material-ui/core/IconButton';
import Avatar from '@material-ui/core/Avatar';
import Container from '@material-ui/core/Container';
import MUILink from '@material-ui/core/Link';
import Snackbar from '@material-ui/core/Snackbar';
import Alert from '@material-ui/lab/Alert';
import AlertTitle from '@material-ui/lab/AlertTitle';
import Collapse from '@material-ui/core/Collapse';
import { CSSTransition } from 'react-transition-group';

import AssessmentIcon from '@material-ui/icons/Assessment';
import { LoginIcon, LogoutIcon } from '../Commons'

import { MoreIcon, LessIcon, UserIcon, HomeIcon, AboutIcon, ObjectIcon, CloudIcon } from '../Commons';
import { AppRouter } from '../../routers/AppRouter';
import { getIsSection_App } from '../../routers/AppRouter';


// App

class App extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;

        this.handleStateChanged = this.handleStateChanged.bind(this);

        this._onStateChangedListener = new OnStateChangedListener(this);

        this.state = {
            jcpFEState: this.jcpFE.getState(),
            showBackdrop: this.jcpFE.getState() != this.jcpFE.StateEnum.READY,
        }
        this.isFirstShow = this.jcpFE.getState()!="READY";
    }

    componentDidMount() {
        this.jcpFE.addOnStateChanged(this._onStateChangedListener);
    }

    componentWillUnmount() {
        this.jcpFE.removeOnStateChanged(this._onStateChangedListener);
    }

    handleStateChanged() {
        const showBackdrop = this.jcpFE.getState() != this.jcpFE.StateEnum.READY;
        this.setState({
            jcpFEState: this.jcpFE.getState(),
            showBackdrop: showBackdrop,
        });
        this.isFirstShow = this.isFirstShow && this.jcpFE.getState()!="READY";
    }

    render () {
        const appRouter = <AppRouter {...this.props} />;
        
        return (
            <div style={{display: 'flex'}}>
                <CssBaseline />
                <JCPFEBackdrop showBackdrop={this.state.showBackdrop} isFirstShow={this.isFirstShow} jcpFEState={this.state.jcpFEState} />
                { this.jcpFE.getState() == this.jcpFE.StateEnum.READY ?
                    <BrowserRouter>
                        <JCPFEAppBar jcpfe={this.jcpFE} title="John Cloud Platform"/>
                        {/* <JCPFEDrawer jcpfe={this.jcpFE} router={appRouter}/> */}
                        <main style={{flexGrow: '1',height: '100vh',overflow: 'auto'}}>
                            <JCPFEAppBarSpacer />
                            <Container maxWidth="lg" style={{paddingTop: this.props.theme.spacing(4), paddingBottom: this.props.theme.spacing(4)}}>
                                {appRouter}
                                <JCPFEFooter homeUrl={this.state} />
                            </Container>
                        </main>
                    </BrowserRouter>
                : null }
                <JCPFESnackBarMessage jcpfe={this.jcpFE} />
            </div>
        );
    }

}
export default withTheme(App);

class OnStateChangedListener {
        
    _owner = null;

    constructor(app) {
        this._owner = app;
    }

    onStateChanged(state) {
        this._owner.handleStateChanged();
    }

}


// Backdrop

class JCPFEBackdropRaw extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            transition: false
        }
    }

    componentDidMount() {
        this.setState({transition: true});
    }

    render() {
        const showBackdrop = this.props.showBackdrop;
        const isFirstShow = this.props.isFirstShow;
        const state = this.props.jcpFEState;
        
        return (
            <Backdrop style={{zIndex: this.props.theme.zIndex.drawer + 2,color: '#fff',}} open={showBackdrop} style={{flexDirection: 'column'}}>
                {isFirstShow
                    ? this.getFirstBackdrop(state)
                    :  <div>
                        <Typography component="p" variant="h6" color="inherit" noWrap gutterBottom>
                            <b>Error:</b> Can't connect to JOSP eco system!
                        </Typography>
                        <Typography component="p" variant="body1" color="inherit">
                            Next connection attempt within 30 seconds<br/>
                            Current client state is: {state}
                        </Typography>
                    </div>
                }
            </Backdrop>
        );
    }

    getFirstBackdrop(state) {
        const backdrop_img_number = Math.floor(Math.random() * 4) + 1;
        //const backdrop_img = "backdrop/backdrop_" + backdrop_img_number + ".jpg";   // random backdrop_[1-4].jpg
        const backdrop_img = "backdrop/backdrop_john_1280.png";
        const css = `
        .backdrop-internal {
            background-color: black;
            position: absolute;
            left: 0px;
            top: 0px;
            z-index: 10;
            width: 100%;
            height: 100%;
        }
        
        .backdrop-img {
            position: absolute;
            left: 0px;
            z-index: 20;
            height: inherit;
            background-image: 
                            
                            radial-gradient(transparent 60%, black 74%),
                            linear-gradient(180deg, transparent 70%, black 75%),
                            linear-gradient(180deg, black 30%, transparent 35%),
                            url('/media/` + backdrop_img + `');
            background-color: black;
            background-size: 100%;
            background-repeat: no-repeat;
            background-attachment: fixed;
            background-position-y: center;
        }
        
        .backdrop-text {
            position: absolute;
            left: 0px;
            top: 0px;
            z-index: 50;
            width: 100%;
            height: 100%;
        }

        .backdrop-text-title {
            position: absolute;
            left: 55%;
            top: 48%;
            width: 40%;

            font-weight: bolder;
            font-size: 2.5em;
        }

        .backdrop-text-slogan {
            position: absolute;
            left: 59%;
            top: 53%;
            width: 40%;

            font-size: 1.2em;
            font-weight: bold;
            font-style: italic;
            color: #c3c3c3;
        }

        .backdrop-text-state {
            position: absolute;
            color: white;
            bottom: 10%;
            width: 100%;
            text-align: center;
        }
            

        
            .backdrop-img {
                transition: opacity 0.5s ease,
                            color 0.5s ease,
                            width 0.5s cubic-bezier(0, 0, 0, 1);
                opacity: 0;
                width: 0;
            }
            .backdrop-img-enter-done {
                opacity: 1;
                width: 100%;
            }

            .backdrop-text {
                transition: opacity 0.30s ease 0.20s;
                opacity: 0;
            }
            .backdrop-text-enter-done {
                opacity: 1;
            }

        `;

        return (
            <React.Fragment>
                <style>{css}</style>
                <div className="backdrop-internal">
                    <CSSTransition
                        in={this.state.transition}
                        timeout={500}
                        classNames="backdrop-img"
                        unmountOnExit
                        >
                        <div className="backdrop-img" />
                    </CSSTransition>
                    <CSSTransition
                        in={this.state.transition}
                        timeout={500}
                        classNames="backdrop-text"
                        unmountOnExit
                        >
                        <div className="backdrop-text" style={{display: 'none'}}>
                            <Typography component="h1" variant="h2" color="inherit" className="backdrop-text-title">
                                John OS Platform
                            </Typography>
                            <Typography component="h2" variant="subtitle1" color="inherit" className="backdrop-text-slogan">
                                Connecting to your John EcoSystem
                            </Typography>
                            <Typography component="p" variant="body1" color="inherit" className="backdrop-text-state">
                                Current client state is: {state}
                            </Typography>
                        </div>
                    </CSSTransition>
                </div>
            </React.Fragment>
        );
    }

}
const JCPFEBackdrop = withTheme(JCPFEBackdropRaw)


// JCPFEAppBar

class JCPFEAppBarRaw extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
    }

    render() {
        const classes = this.props.classes;
        
        return (
            <AppBar position="absolute" style={{
                    background: 'linear-gradient(-90deg, #92dacb 0%, #3064b7 100%)',
                    zIndex: this.props.theme.zIndex.drawer + 1,
                }}>
                <Toolbar style={{paddingRight: '24'}}>
                    <Link to="/">
                        <img src="/media/logo_250.png" style={{height: '32px',marginRight: '16px'}} />
                    </Link>
                    <Typography component="h1" variant="h6" color="inherit" noWrap style={{flexGrow: '1'}}>
                        {this.props.title}
                    </Typography>
                    <JCPFEUserMenu jcpfe={this.jcpFE} />
                </Toolbar>
            </AppBar>
        );
        
    }

}
const JCPFEAppBar = withTheme(JCPFEAppBarRaw)

class JCPFEAppBarSpacer extends React.Component {

    render() {
        return (
            <div style={{
                minHeight: '56px',
                }} />
        );
    }
}

// JCPFEUserMenu

class JCPFEUserMenu extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.state = {
            anchorEl: null
        }
    }

    handleClick(event) {
        this.setState({anchorEl: event.currentTarget});
    }

    handleClose() {
        this.setState({anchorEl: null});
    }

    render() {
        const classes = this.props.classes;
        const openDrawer = this.props.openDrawer;
        return (
            <div>
                {this.jcpFE.getUser().isAuthenticated()
                    ?
                        <Avatar
                            alt={this.jcpFE.getUser().getName()}
                            src="/not-existing.jpg" 
                            color="primary"
                            onClick={(event) => this.handleClick(event)} 
                            style={{
                                color: 'white',
                                backgroundColor: '#3064b7'
                            }}/>
                    :
                        <Avatar
                            color="primary"
                            onClick={(event) => this.handleClick(event)}
                            style={{
                                color: 'white',
                                backgroundColor: '#3064b7'
                            }} >
                            <UserIcon />
                        </Avatar>
                }
                <Menu
                    anchorEl={this.state.anchorEl}
                    keepMounted
                    open={Boolean(this.state.anchorEl)}
                    onClose={() => this.handleClose()}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'center',
                    }}
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'center',
                    }}
                >
                    {this.jcpFE.getUser().isAuthenticated()
                        ?   <MenuItem component={Link} to='/user' >
                                <ListItemIcon>
                                    <UserIcon fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary="Profile" />
                            </MenuItem>
                        : null}
                    {this.jcpFE.getUser().isAuthenticated()
                        ?   <MenuItem component='a' href={this.jcpFE.getJSLWBUrls().userLogout} >
                                <ListItemIcon>
                                    <LogoutIcon fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary="Logout" />
                            </MenuItem>
                        : null}
                    {!this.jcpFE.getUser().isAuthenticated()
                        ?   <MenuItem component='a' href={this.jcpFE.getJSLWBUrls().userLogin} >
                                <ListItemIcon>
                                    <LoginIcon fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary="Login" />
                            </MenuItem>
                        : null}
                    {!this.jcpFE.getUser().isAuthenticated()
                        ?   <MenuItem component='a' href={this.jcpFE.getJSLWBUrls().userRegistration} >
                                <ListItemIcon>
                                    <SendIcon fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary="Register" />
                            </MenuItem>
                        : null}
                </Menu>
            </div>
        );
        
    }

}


// JCPFEDrawer

class JCPFEDrawerRaw extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
    }

    render() {
        const {isHome, isObjects, isService, isJCP, isStats} = getIsSection_App();

        const mainListItems = (
            <div>
                <Link to="/">
                    <ListItem button selected={isHome}>
                        <ListItemIcon style={{minWidth: 'unset'}}>
                            <Tooltip title="Home" ><HomeIcon /></Tooltip>
                        </ListItemIcon>
                    </ListItem>
                </Link>
                <Link to="/objects">
                    <ListItem button selected={isObjects}>
                        <ListItemIcon style={{minWidth: 'unset'}}>
                            <Tooltip title="Objects list" ><ObjectIcon /></Tooltip>
                        </ListItemIcon>
                    </ListItem>
                </Link>
                <Link to="/service">
                    <ListItem button selected={isService}>
                        <ListItemIcon style={{minWidth: 'unset'}}>
                            <Tooltip title="About" ><AboutIcon /></Tooltip>
                        </ListItemIcon>
                    </ListItem>
                </Link>
                <Divider />
                { this.jcpFE.getUser().isAdmin()
                    ?   <Link to="/jcp">
                            <ListItem button selected={isJCP}>
                                <ListItemIcon style={{minWidth: 'unset'}}>
                                    <Tooltip title="JCP Management" ><CloudIcon /></Tooltip>
                                </ListItemIcon>
                            </ListItem>
                        </Link>
                    : null }
                { this.jcpFE.getUser().isAdmin()
                    ?   <Divider />
                    : null }
                { this.jcpFE.getUser().isAdmin()
                    ?   <Link to="/stats">
                            <ListItem button selected={isStats}>
                                <ListItemIcon style={{minWidth: 'unset'}}>
                                    <Tooltip title="JCP FE Client stats (DEBUG)" ><AssessmentIcon /></Tooltip>
                                </ListItemIcon>
                            </ListItem>
                        </Link>
                    : null }
                
            </div>
          );
          
        return (
            <Drawer
                variant="permanent"
                style={{
                    position: 'relative',
                    whiteSpace: 'nowrap',
                    overflowX: 'hidden',
                    width: '56px',
                }}
                >
                <JCPFEAppBarSpacer />
                <List>{mainListItems}</List>
            </Drawer>
        );
        
    }

}
export const JCPFEDrawer = withTheme(JCPFEDrawerRaw)

export class JCPFEDrawerSpacer extends React.Component {

    render() {
        return (
            <div style={{
                minWidth: '56px',
                minHeight: '1px',
                ...this.props.style
                }} />
        );
    }
}


// JCPFEFooter

class JCPFEFooter extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const classes = this.props.classes;
        const openDrawer = this.props.openDrawer;
        
        return (
            <Box pt={4}>
                <Typography variant="body2" color="textSecondary" align="center">
                    Copyright ©{' '}
                    <MUILink href={this.props.homeUrl}>
                        John O.S. Project
                    </MUILink>{' '}
                    {new Date().getFullYear()}
                </Typography>
            </Box>
        );
        
    }

}


// JCPFESnackBar

class JCPFESnackBar extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.jcpFE.registerSnackBar(this);
        this.state = {
            open: false,
            component: <p>No message specified</p>,
            action: null,
            autoHideMs : 5000
        }
    }

    showComponent(component, action=null, autoHideMs=null) {
        this.setState({
            open: true,
            component: component,
            action: action,
            autoHideMs : autoHideMs ? autoHideMs : 5000
        });
    }

    _handleClose(event, reason) {
        if (reason === 'clickaway')
            return;

        this.setState({ open: false });
    };

    render() {
        return (
            <Snackbar
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                }}
                open={this.state.open}
                autoHideDuration={this.state.autoHideMs}
                onClose={() => this._handleClose()}
                action={this.state.action}
                >
                    {this.state.component}
            </Snackbar>
        );
    }

}


class JCPFESnackBarMessage extends JCPFESnackBar {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.jcpFE.registerSnackBar(this);
        this.state = {
            severity: "info",
            title: "No title",
            message: "No message specified",
            details: null,
            expanded: false,
            autoHideMs : 5000
        }
    }

    showMessage(severity, title, message, details=null, action=null, autoHideMs=null) {
        this.setState({
            open: true,
            severity: severity,
            title: title,
            message: message,
            details: details,
            action: action,
            autoHideMs : autoHideMs ? autoHideMs : this.state.autoHideMs
        });
    }

    _handleExpand() {
        this.setState({expanded: !this.state.expanded});
    };

    render() {
        this.state.component = this._getAlert(this.state.severity,this.state.title,this.state.message,this.state.details);
        return super.render();
    }

    _getAlert(severity, title, message, details=null) {
        return (
            <Alert
                elevation={6}
                variant="filled" 
                onClose={() => this._handleClose()}
                severity={severity}>
                <AlertTitle>
                    {title}
                    {!message && details
                        ? this._getDetails(details)
                        : null}
                </AlertTitle>
                {message ? message : null}
                {message && details
                    ? this._getDetails(details)
                    : null }
            </Alert>
        );
    }

    _getDetails(details) {
        return (
            <React.Fragment>
                <IconButton onClick={() => this._handleExpand()}>
                    {this.state.expanded ? <LessIcon style={{ color: 'white' }} fontSize="inherit" /> : <MoreIcon style={{ color: 'white' }} fontSize="inherit" />}
                </IconButton>
                <Collapse in={this.state.expanded} timeout="auto" unmountOnExit>
                    {details}
                </Collapse>
            </React.Fragment>
        );
    }

}
