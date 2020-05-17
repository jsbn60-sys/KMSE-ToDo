<template id="new-category">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card border-info">
                    <div class="card-header-tab card-header text-center">
                        <div class="card-header-title font-size-lg font-weight-normal"><i class="fa fa-tasks"></i><h5 class="display-5">Create a new Category</h5></div>
                    </div>
                    <div class="scroll-area-sm bg-info">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li class="list-group-item">
                                            <div class="todo-indicator"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left">
                                                        <div class="form-group">
                                                            <label for="title">Title</label>
                                                            <input v-model="title" type="text" class="form-control" id="title" placeholder="Insert the title of your category here">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="color">Color</label>
                                                            <select v-model="color" class="form-control" id="color">
                                                                <option>green</option>
                                                                <option>yellow</option>
                                                                <option>blue</option>
                                                                <option>orange</option>
                                                                <option>red</option>
                                                            </select>
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
                    <div class="d-block text-center card-footer"><button class="btn btn-info" v-on:click="newCategory()">Create</button></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("new-category", {
        template: "#new-category",
        data: () => ({
            token: localStorage.getItem("token"),
            title: "",
            color: "",
        }),
        methods: {
            newCategory() {
                let token = this.token;
                let title = this.title;
                let color = this.color;
                fetch("/api/me/categories?token=" + token, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "name=" + title
                })
                    .then(async res => { if(res.status === 200) location.pathname = "/tasks" })
                    .catch(() => alert("Error while creating new category"));
            }
        },
    });
</script>
