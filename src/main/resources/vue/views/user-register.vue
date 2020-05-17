<template id="user-register">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card border-info">
                    <div class="card-header-tab card-header text-center">
                        <div class="card-header-title font-size-lg font-weight-normal"><i class="fa fa-tasks"></i><h5 class="display-5">Register for your lists</h5></div>
                    </div>
                    <div class="scroll-area-sm">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li class="list-group-item">
                                            <div class="todo-indicator bg-warning"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left">
                                                        <div class="form-group">
                                                            <label for="username">Username</label>
                                                            <input v-model="username" type="text" class="form-control" id="username" placeholder="insert your username">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="password">E-Mail</label>
                                                            <input v-model="email" type="email" class="form-control" id="email" placeholder="insert your email address">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="password">Password</label>
                                                            <input v-model="password" type="password" class="form-control" id="password" placeholder="insert your password">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </perfect-scrollbar>
                    </div>
                    <div class="d-block text-center card-footer"><button class="btn btn-info" v-on:click="register()">Register</button></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("user-register", {
        template: "#user-register",
        data: () => ({
            username: "",
            email: "",
            password: ""
        }),
        methods: {
            register() {
                let username = this.username;
                let email = this.email;
                let password = this.password;
                fetch("/api/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "username=" + username + "&email=" + email + "&password=" + password
                })
                    .then(async res => { if(res.status === 200) {
                        const data = await res.json();
                        location.pathname = "/login"
                    }})
                    .catch(() => alert("Error while register"));
            }
        },
    });
</script>
